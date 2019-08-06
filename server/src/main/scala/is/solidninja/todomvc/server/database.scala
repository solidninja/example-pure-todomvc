package is
package solidninja
package todomvc
package server

import java.util.UUID

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import cats.instances.list._
import cats.syntax.traverse._
import doobie._
import doobie.h2._
import doobie.h2.implicits._
import doobie.implicits._
import is.solidninja.todomvc.protocol._
import is.solidninja.todomvc.server.ApiModel.NewTodo

object DbConnection {

  /**
    * Create a Transactor pointing to a new H2 in-memory database that is not destroyed upon closing
    */
  def h2[F[_]: Async: ContextShift](name: String = "test"): Resource[F, H2Transactor[F]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[F] // our transaction EC
      block = Blocker.liftExecutionContext(te)
      xa <- H2Transactor.newH2Transactor[F](s"jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1", "sa", "", ce, block)
      _ <- Resource.liftF(xa.setMaxConnections(10))
    } yield xa
}

trait TodoDatabase[F[_]] {
  val init: F[Unit]
  val list: F[List[Todo]]
  def find(id: UUID): F[Option[Todo]]
  def save(todo: Todo): F[Todo]
  def save(todo: NewTodo): F[Todo]
  def delete(id: UUID): F[Boolean]
  def sync(todos: List[Todo]): F[List[Todo]]
}

class DoobieTodoDatabase[F[_]: Sync](xa: Transactor[F]) extends TodoDatabase[F] with TodoDatabaseQueries {

  val init: F[Unit] = {
    val action = for {
      _ <- createTableQuery.run
      _ <- doSave(NewTodo("Create demo webapp", completed = false).withRandomUuid)
      _ <- doSave(NewTodo("Learn Typelevel libraries", completed = false).withRandomUuid)
      _ <- doSave(NewTodo("Take over world", completed = false).withRandomUuid)
    } yield ()
    action.transact(xa)
  }

  val list: F[List[Todo]] =
    listQuery.to[List].transact(xa)

  def find(id: UUID): F[Option[Todo]] =
    findQuery(id).option.transact(xa)

  def save(todo: Todo): F[Todo] =
    doSave(todo).transact(xa)

  def save(todo: NewTodo): F[Todo] =
    doSave(todo.withRandomUuid).transact(xa)

  def delete(id: UUID): F[Boolean] =
    doDelete(id).transact(xa)

  def sync(todos: List[Todo]): F[List[Todo]] = {
    val action = for {
      _ <- doDeleteAll
      todos <- todos.traverse(doSave)
    } yield todos
    action.transact(xa)
  }

  private def doSave(todo: Todo): ConnectionIO[Todo] =
    saveQuery(todo).run.map(_ => todo)

  private def doDelete(id: UUID): ConnectionIO[Boolean] =
    deleteQuery(id).run.map(_ > 0)

  private def doDeleteAll: ConnectionIO[Boolean] =
    deleteAllQuery.run.map(_ > 0)
}

private[server] trait TodoDatabaseQueries {
  val createTableQuery: Update0 =
    sql"""
    create table todos(
      title varchar not null,
      completed boolean not null,
      id UUID primary key
    );
    """.update

  val listQuery: Query0[Todo] =
    sql"""
    select id, title, completed from todos
    """.query[Todo]

  def findQuery(id: UUID): Query0[Todo] =
    sql"""
    select id, title, completed from todos where id = $id
    """.query[Todo]

  def saveQuery(todo: Todo): Update0 =
    sql"""
    merge into todos (id, title, completed) key (id)
    values (${todo.id}, ${todo.title}, ${todo.completed});
    """.update

  def deleteQuery(id: UUID): Update0 =
    sql"""
    delete from todos where id = $id
    """.update

  val deleteAllQuery: Update0 =
    sql"""
    delete from todos
    """.update
}
