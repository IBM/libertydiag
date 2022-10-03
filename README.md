# libertydiag

`libertydiag` is a Jakarta EE 9 and MicroProfile 5 application for simulating diagnostic situations.

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
1. Open browser to <http://localhost:9080/libertydiag/api/helloworld/execute> or <https://localhost:9443/libertydiag/api/helloworld/execute>
