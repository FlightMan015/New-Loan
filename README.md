### Loan
 
The loan application backend provides an API to request and accept loan offers. 
 
## Motivation

Loan requests are delegated to various banking APIs in order to provide 
multiple offers to the customer. The loan backend takes care of integrating those 
3rd party APIs as well as request and response validation. 

## Build Info

- [Info Staging](https://staging-loan.joonko-bonify.de/loan/info)
- [Health_Staging](https://staging-loan.joonko-bonify.de/loan/health)

## API Reference

- [Swagger Staging](https://staging-loan.joonko-bonify.de/swagger-ui/index.html)

## Tests

`mvn test` runs unit tests.

`mvn verify` runs unit and integration tests.

`mvn failsafe:integration-test` runs integration tests only.

## Local Setup
Copy the `application-local-example.properties` to `application-local.properties` and update  
the passwords from 1password 

## Start local mongo db docker image
`docker run -p 27017:27017 mongo`  

## To enable netty request logging 
logging.level.reactor.netty.http.client=DEBUG

## To run application in local docker container

- Change 'spring.data.mongodb.host' to 'api-database' in local properties file.
- Change ENTRYPOINT in Dockerfile to ["java", "-Dspring.profiles.active=local", "-jar", "/app.jar"] so that latest profile is picked up.
- Do a maven clean install to generate latest jar file.
- Run 'docker build -t loan .' from project root folder to generate latest image.
- Now run command 'docker-compose up' so that docker-compose.yml file spins up the container.
- Application is accessible at 'localhost:9091'.
- If you want to run 'loan-demand.http' to get the loan offers, make sure port is changed.
