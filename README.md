# Chronos

RESTfull API to show the power of Java and Spring Boot. Basically you can register companies, employees and manage a company’s time attendance.

## Features

* Spring Boot 2.1.4.RELEASE
* Continuous integration with TravisCI
* PostgreSQL database
* H2 to run tests
* BCrypt password encryption
* Flyway database versioning
* Hikari JDBC connection pooling framework
* Swagger and Swagger UI
* JWT (Json Web Token) Authentication
* EhCache
* Deploy to Heroku

## Installing

```
git clone https://github.com/Ruscigno/Ponto-Inteligente-java-api.git
mvn compile
```

### Running the tests

```
mvn test
```

## API Authentication

Default authentication. Send a post request to /auth
```
{
	"email":"admin@ruscigno.com",
	"senha":"123456"
}
```

## Authors

* **Sander Ruscigno** - [ruscigno.com.br](https://www.ruscigno.com.br)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details