package is
package solidninja
package todomvc
package server

import java.util.UUID

import cats.effect._
import cats.implicits._
import doobie._
import doobie.h2._
import doobie.h2.implicits._
import doobie.implicits._
import is.solidninja.todomvc.protocol._

trait TodoDatabase {
  val init: IO[Unit]
  val list: IO[List[Todo]]
  def find(id: UUID): IO[Option[Todo]]
  def save(todo: Todo): IO[Todo]
  def delete(id: UUID): IO[Boolean]
  def sync(todos: List[Todo]): IO[List[Todo]]
}

object DoobieTodoDatabase {

  def h2Transactor(implicit cs: ContextShift[IO]): Resource[IO, H2Transactor[IO]] =
    for {
      ce <- ExecutionContexts.fixedThreadPool[IO](32) // our connect EC
      te <- ExecutionContexts.cachedThreadPool[IO] // our transaction EC
      block = Blocker.liftExecutionContext(te)
      xa <- H2Transactor.newH2Transactor[IO]("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "", ce, block)
    } yield xa

  def create(implicit cs: ContextShift[IO]): Resource[IO, DoobieTodoDatabase] =
    h2Transactor.evalMap { xa =>
      for {
        _ <- xa.setMaxConnections(10)
        db = new DoobieTodoDatabase(xa)
        _ <- db.init
      } yield db
    }
}

class DoobieTodoDatabase(xa: Transactor[IO]) extends TodoDatabase with TodoDatabaseQueries {

  val init: IO[Unit] = {
    val action = for {
      _ <- createTableQuery.run
      _ <- doSave(Todo("Create demo webapp", completed = false))
      _ <- doSave(Todo("Learn Typelevel libraries", completed = false))
      _ <- doSave(Todo("Take over world", completed = false))
    } yield ()
    action.transact(xa)
  }

  val list: IO[List[Todo]] =
    listQuery.to[List].transact(xa)

  def find(id: UUID): IO[Option[Todo]] =
    findQuery(id).option.transact(xa)

  def save(todo: Todo): IO[Todo] =
    doSave(todo).as(todo).transact(xa)

  def delete(id: UUID): IO[Boolean] =
    doDelete(id).transact(xa)

  def sync(todos: List[Todo]): IO[List[Todo]] = {
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
    select title, completed, id from todos
    """.query[Todo]

  def findQuery(id: UUID): Query0[Todo] =
    sql"""
    select title, completed, id from todos where id = $id
    """.query[Todo]

  def saveQuery(todo: Todo): Update0 =
    sql"""
    merge into todos (title, completed, id) key (id)
    values (${todo.title}, ${todo.completed}, ${todo.id});
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
