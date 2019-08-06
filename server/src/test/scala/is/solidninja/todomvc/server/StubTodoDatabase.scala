package is
package solidninja
package todomvc
package server

import java.util.UUID

import cats.Applicative
import cats.effect.{Resource, Sync}
import cats.effect.concurrent.Ref
import cats.syntax.applicative._
import cats.syntax.functor._
import is.solidninja.todomvc.protocol.Todo

class StubTodoDatabase[F[_]: Applicative](val mapRef: Ref[F, Map[UUID, Todo]]) extends TodoDatabase[F] {
  override val init: F[Unit] = ().pure[F]
  override val list: F[List[Todo]] = mapRef.get.map(_.values.to(List))
  override def find(id: UUID): F[Option[Todo]] = mapRef.get.map(_.get(id))
  override def save(todo: Todo): F[Todo] = mapRef.modify(_.updated(todo.id, todo) -> todo)
  override def save(todo: ApiModel.NewTodo): F[Todo] = save(todo.withRandomUuid)
  override def delete(id: UUID): F[Boolean] = mapRef.modify(map => map.removed(id) -> map.contains(id))
  override def sync(todos: List[Todo]): F[List[Todo]] =
    mapRef.modify(
      map =>
        todos.foldLeft(Map.empty[UUID, Todo]) {
          case (m, todo) => m.updated(todo.id, todo)
        } -> map.values.to(List)
    )
}

object StubTodoDatabase {
  def apply[F[_]: Sync]: Resource[F, StubTodoDatabase[F]] =
    Resource.liftF(Ref[F].of(Map.empty[UUID, Todo])).map(new StubTodoDatabase[F](_))
}
