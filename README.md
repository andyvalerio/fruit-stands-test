# fruit-stands-test

Fruit stands test is a Spring Boot application. <br>

To build it and run it you need Apache Maven and a JDK (at least version 11).

## Build and run
Build the project by running:<br> 
_mvn clean install_

Run the application by executing: <br>
_mvn spring-boot:run_

## Scenarios
There is a REST endpoint for each of the test scenarios. 
Calling an endpoint will create the new scenario (customers, stands and 
fruit baskets) and execute the scenario. That is, calculate and print out 
the results of the scenarios.

### Endpoints
While you are running the application locally you can reach the endpoints here: 
- http://localhost:8080/fruit/base-scenario
- http://localhost:8080/fruit/extension-1-2
- http://localhost:8080/fruit/extension-3
- http://localhost:8080/fruit/extension-4

Extensions 1 and 2 are together because they are functionally the same. <br>
These endpoints are reactive; they use an internet protocol called JSON
streaming to gradually print the setup and results on a web page.
Reach the endpoints from **any browser** or curl from a command line.

## Configuration
It's possible to configure the behaviour of this application 
by changing the values in application.yml under the _resources_ folder.<br>
For example it's possible to change the maximum price of the fruit baskets
and the number of stands the friends encounter along the way.

###Known limitations
Because of a limitation of the reactive framework used,
the maximum number of stands supported is 23.
This can be worked around by rewriting the code that prints
the events in the scenarios, but it's outside the scope 
and purpose of this test.