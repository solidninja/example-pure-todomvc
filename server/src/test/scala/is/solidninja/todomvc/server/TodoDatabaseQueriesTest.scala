package is.solidninja.todomvc.server

import java.util.UUID

import doobie.implicits._
import doobie.scalatest._
import is.solidninja.todomvc.protocol.Todo
import org.scalatest.{FreeSpec, Matchers}

class TodoDatabaseQueriesTest extends FreeSpec with Matchers with IOChecker with TodoDatabaseQueries {

  implicit val cs = cats.effect.IO.contextShift(scala.concurrent.ExecutionContext.global)

  override val transactor = (for {
    xa <- DoobieTodoDatabase.h2Transactor.allocated.map(_._1)
    _ <- createTableQuery.run.transact(xa)
  } yield xa).unsafeRunSync()

  val sampleTodo = Todo("check query in doobie unit test", completed = true)

  "listQuery" in check(listQuery)
  "findQuery" in check(findQuery(UUID.randomUUID()))

  // FIXME: checking merge into causes a type error
  "saveQuery" ignore check(saveQuery(sampleTodo))

  "deleteQuery" in check(deleteQuery(sampleTodo.id))
  "deleteAllQuery" in check(deleteAllQuery)

}
