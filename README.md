# Scrum poker

## Info
SP or Scrum Poker is a kind of pet project. It's designed to allow user votes with dividing into groups.

It's also supports voting with a single common group.

For user identification used primitive authorization, before using the application user has to proceed with the registration.

You can set a secret code for the voting room if you don't want any strangers.

For invite your friend or folks just send them room link (and secret code if it was set).

If your mate is not registered or authorized he has to do it first.

## Application properties
```properties
app.session.ttl.type=DAYS
app.session.ttl.value=30
```

For `ttl.type` used enum values: `java.time.temporal.ChronoUnit`, for example:
- DAYS
- SECONDS
- MILLIS

## Life demo

You can see how it works here: http://45.67.230.124/

## Service endpoint

### Endpoint `/actuator/info`
Provides arbitrary application info.

### Endpoint `/actuator/health`
The health endpoint provides basic application health information. Implementation of this endpoint is custom and
complies specification.

## Build
To build project and get executable files, run command:

```
./gradlew clean release installBootDist
```

After that you can see executable files in this directory: `build/install/sp-boot/bin`.

To run project you should determine environments (for DB at least) or use properties files.

Use command `./buid/install/sp-boot/bin/sp` to launch the application.

## Dockerization
Service must be ready to be dockerized.

It's up to you how to feed it with properties, but using docker-compose is a good idea (see docker-compose.yml).

If there is CI integration (it should be there) images are been registering for every git tag

## DB
Service works with PostgresDB

### Properties
Properties for DB configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/scrum_poker
spring.datasource.username = scrum_poker
spring.datasource.password = a12345
spring.jpa.properties.hibernate.default_schema = scrum_poker
```

### Flyway
Project use flyway as migration library.

For turning on migration you should set this properties:
```properties
spring.flyway.enabled=true
spring.flyway.schemas=scrum_poker
```

## TO-DO:
- Need to write tests: unit + behaviour. In future better use TDD.
- If somebody unauthorized open join link save info about it and after authorization redirect to join page. 
