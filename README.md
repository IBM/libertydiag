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
1. Check various components:
    * REST API over HTTP: <http://localhost:9080/api/helloworld/execute>
    * REST API over HTTPS: <https://localhost:9443/api/helloworld/execute>
    * HTTP Servlet: <http://localhost:9080/HelloWorldServlet>
    * MicroProfile Health: <http://localhost:9080/health>
    * MicroProfile Metrics: <http://localhost:9080/metrics>
    * MicroProfile OpenAPI: <http://localhost:9080/openapi/ui/>
    * MBeans: <https://localhost:9443/IBMJMXConnectorREST/mbeans> (user = admin, password = password)
