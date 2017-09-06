package is.solidninja.todomvc.server

import java.util.UUID

import cats.effect.IO
import doobie.h2._
import doobie.implicits._
import doobie.scalatest._
import is.solidninja.todomvc.protocol.Todo
import org.scalatest.{FreeSpec, Matchers}

class TodoDatabaseQueriesTest extends FreeSpec with Matchers with IOChecker with TodoDatabaseQueries {

  override val transactor = unsafeRunSync(for {
    xa <- H2Transactor[IO]("jdbc:h2:mem:todo_test;DB_CLOSE_DELAY=-1", "sa", "")
    _ <- createTableQuery.run.transact(xa)
  } yield xa)

  val sampleTodo = Todo("check query in doobie unit test", completed = true)

  "listQuery" in check(listQuery)
  "findQuery" in check(findQuery(UUID.randomUUID()))

  // FIXME: checking merge into causes a type error
  "saveQuery" ignore check(saveQuery(sampleTodo))

  "deleteQuery" in check(deleteQuery(sampleTodo.id))
  "deleteAllQuery" in check(deleteAllQuery)

}
