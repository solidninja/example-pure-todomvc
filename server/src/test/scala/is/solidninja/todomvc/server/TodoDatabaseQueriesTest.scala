package is
package solidninja
package todomvc
package server

import java.util.UUID

import cats.effect.{ContextShift, IO}
import cats.syntax.functor._
import doobie.implicits._
import doobie.scalatest._
import doobie.util.transactor.Transactor
import is.solidninja.todomvc.server.ApiModel.NewTodo
import org.scalatest.{FreeSpec, Matchers}

class TodoDatabaseQueriesTest extends FreeSpec with Matchers with IOChecker with TodoDatabaseQueries {

  implicit val cs: ContextShift[IO] = cats.effect.IO.contextShift(scala.concurrent.ExecutionContext.global)

  override val transactor: Transactor[IO] = DbConnection
    .h2[IO](s"test-${UUID.randomUUID()}")
    .evalMap(xa => createTableQuery.run.transact(xa).as(xa))
    .allocated
    .unsafeRunSync()
    ._1

  val sampleTodo = NewTodo("check query in doobie unit test", completed = true).withRandomUuid

  "listQuery" in check(listQuery)
  "findQuery" in check(findQuery(UUID.randomUUID()))

  // FIXME: checking merge into causes a type error
  "saveQuery" ignore check(saveQuery(sampleTodo))

  "deleteQuery" in check(deleteQuery(sampleTodo.id))
  "deleteAllQuery" in check(deleteAllQuery)

}
