[![License: GPL v3](https://img.shields.io/badge/license-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
[![CircleCI](https://circleci.com/gh/bob-cd/bob.svg?style=svg)](https://circleci.com/gh/bob-cd/bob)
[![Dependencies Status](https://versions.deps.co/bob-cd/bob/status.png)](https://versions.deps.co/bob-cd/bob)

[![Join the chat at https://gitter.im/bob-cd/bob](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/bob-cd/bob)

# Bob the Builder

## This is what CI/CD should've been.

### [Why](https://github.com/bob-cd/bob/blob/master/docs/rationale.md) Bob

## Build requirements
- Any OS supporting Java and Docker
- JDK 8+ (latest preferred for optimal performance)
- [Leiningen](https://leiningen.org/) 2.0+

## Running requirements
- Any OS supporting Java and Docker
- JRE 8+ (latest preferred for optimal performance)
- Docker (latest preferred for optimal performance)

## Testing, building and running
- Clone this repository.
- Install the Build requirements.
- Following steps **need Docker**:
    - Run `lein test` to run tests.
    - Run `lein uberjar` to get the standalone JAR.
    - Run `java -jar ./target/bob-standalone.jar` to start the server on port **7777**.

### Extensive Usage + API [docs](https://bob-cd.readthedocs.io/en/latest/)
