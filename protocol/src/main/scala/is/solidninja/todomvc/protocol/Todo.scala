package is
package solidninja
package todomvc
package protocol

import java.util.UUID

final case class Todo(id: UUID, title: String, completed: Boolean)
