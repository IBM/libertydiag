# libertydiag

`libertydiag` is a [Jakarta EE 9](https://openliberty.io/docs/latest/jakarta-ee.html) and [MicroProfile 5](https://openliberty.io/docs/latest/microprofile.html) web application for simulating diagnostic situations.

Running this application in production should be done with care because it may be used to execute various insecure functions.

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

To develop in Eclipse, click File } Import... } Maven } Existing Maven Projects

### Tips

1. Build WAR file and Liberty package: `mvnw package`
1. Build container: `mvnw deploy`

### Build Issues

1. Running `mvnw deploy` errors with `An Ant BuildException has occured: Execute failed: java.io.IOException: Cannot run program "docker"`
   \
   \
   If `docker` is not on your path and you want to use, for example, `podman` instead:
   ```
   mvnw -Dimage.builder=podman deploy
   ```
1. Running `mvnw deploy` errors with `An Ant BuildException has occured: exec returned:`
   \
   \
   Add `-X` to show full output:
   ```
   mvnw -X deploy
   ```
