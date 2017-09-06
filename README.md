# WIP Pure TodoMVC

Sandbox repo containing a Todo list app implemented using pure functional programming libraries wherever possible.

This is heavily based on [Dave Gurnell's typevel-todomc][typelevel-todomvc] but has been updated for the latest 
libraries in the Typelevel community as of the writing


## Missing features FIXME

* GUI is not implemented
* No tests for REST API client

## Running

The app is implemented in two parts:

 - a server written using [http4s], [Circe][circe], [Doobie][doobie], and [H2][h2];
 - a client, _which is not written yet!_

## License

This project is licensed as [MIT][mit-license]. The original project by Dave Gurnell (from which some code) was taken 
initially was licensed under the [Apache 2][apache-license] license.

[circe]: https://github.com/travisbrown/circe
[doobie]: https://github.com/tpolecat/doobie
[http4s]: http://http4s.org
[h2]: http://www.h2database.com
[typelevel-todomvc]: https://github.com/davegurnell/typelevel-todomvc
[apache-license]: http://www.apache.org/licenses/LICENSE-2.0
[mit-license]: https://opensource.org/licenses/MIT
