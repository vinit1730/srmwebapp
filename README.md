# Student API

A REST API for creating, reading, updating, and deleting students. It uses Java 25, Spring Boot, Spring Data JPA, Bean Validation, and an in-memory H2 database.

## Run

Install Java 25 (the latest LTS release) and Maven, then run:

```shell
mvn spring-boot:run
```

The API starts at `http://localhost:8080`. The H2 database is in memory and is reset when the application stops. The H2 console is available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:studentdb`, username `sa`, and an empty password.

## API

All student operations use `/api/students`.

| Method | Path | Description | Success |
| --- | --- | --- | --- |
| `GET` | `/api/students` | List students, ordered by ID | `200` |
| `GET` | `/api/students/{id}` | Get one student | `200` |
| `POST` | `/api/students` | Create a student | `201` |
| `PUT` | `/api/students/{id}` | Replace a student's details | `200` |
| `DELETE` | `/api/students/{id}` | Delete a student | `204` |

Create and update requests use this JSON shape:

```json
{
  "name": "Ada Lovelace",
  "email": "ada@example.com",
  "age": 20,
  "course": "Computer Science"
}
```

Names and courses are required (up to 100 characters), email must be valid and unique, and age must be between 1 and 120. Emails are stored in lowercase. Invalid requests return `400`, missing students return `404`, and duplicate emails return `409` with a Problem Details response.

## Test

```shell
mvn test
```
