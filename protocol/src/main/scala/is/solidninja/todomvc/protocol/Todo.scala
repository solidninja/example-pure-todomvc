package is
package solidninja
package todomvc
package protocol

import java.util.UUID

case class Todo(title: String, completed: Boolean, id: UUID = UUID.randomUUID)

object Todo {
  def create = Todo(title = "", completed = false)
}
