# Monese Test

This is a sandbox Spring Boot application following the spec given for the Monese technical test.

### Requirements

- Java 11
- MySQL Server
- Gradle (wrapper included)
- Docker (optional)

### Running

Firstly make sure that you have MySQL Server running on your machine, without this the application will not start.

There are many ways to run the application, the most important part is to connect to a valid database.
To do this you need to edit `src/main/resources/application.properties` and change the username, password and database accordingly to your setup.
e.g. `create database monese;`

#### JAR

The simplest method to run the application is to use the built jar

First build the application: `gradlew build`

Then run the jar using Java: `java -jar build/libs/*.jar`

You may skip the above build steps and use a pre-built release package which can be found here: https://github.com/redrails/MoneseTest/releases/tag/1.0
(please ensure that your database is set up accordingly).

#### Gradle

You can also use gradle: `gradlew bootRun`

### Endpoints

**Create an account:**

HTTP PUT - `/account/create` - Requires a JSON body, e.g:

```json
{
    "balance": "0.01"
}
```

---

**Get account balance/transactions:**

HTTP GET - `/account/balance?id={id}` - Where `{id}` is request parameter with a valid Account ID.

---

**Create a transaction:**

HTTP PUT - `/transaction/create` - Requires a JSON body, e.g:

```json
{
    "sender": "1",
    "recipient": "2",
    "amount": 15.0
}
```

Note: Account must have enough balance to complete the transaction.

A Postman collection and environment is included in the repo which you can simply import into Postman and run the endpoints yourself.

### Tests

Run unit tests: `gradlew test` - The unit test coverage is very close to 100% for relevant methods that require it.

End-to-end testing:

An end-to-end test has been created using an in-memory h2 database, you can find this in `tests/MoneseTestIntegrationTests.java`.

The test executes the following steps: 

1. Create an account (a) with a balance of `10.00`
2. Create an account (b) with a balance of `100.00`
3. (a) sends (b) `0.55`
4. (a) sends (b) `3.24`
5. (b) sends (a) `100.00`
6. (b) sends (a) `1000.00` (which is rejected due to insufficient balance)
7. (a) checks balance to be `106.21` (`10.00 - 0.55 - 3.24 + 100.00`)
8. (b) checks balance to be `3.79` (`100.00 + 0.55 + 3.24 - 100.00`)