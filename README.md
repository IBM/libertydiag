# libertydiag

`libertydiag` is a [Jakarta EE](https://openliberty.io/docs/latest/jakarta-ee.html) 9 and [MicroProfile](https://openliberty.io/docs/latest/microprofile.html) 5 web application for simulating diagnostic situations.

## Pre-requisites

* Java >= 8
* Liberty >= 21.0.0.12

## Development

### Basics

1. Build and run with [`mvnw liberty:dev`](https://openliberty.io/docs/latest/development-mode.html):
    * macOS and Linux:
      ```
      ./mvnw liberty:dev
      ```
    * Windows:
      ```
      mvnw liberty:dev
      ```
1. Wait for the message, "server is ready to run a smarter planet". For example:
   ```
   [INFO] [AUDIT   ] CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 30.292 seconds.
   ```
1. Open your browser to the HTTP or HTTPS page:
    * <http://localhost:9080/>
    * <https://localhost:9443/>
