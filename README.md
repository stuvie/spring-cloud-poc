# README #

This **spring-cloud-poc** serves to demonstrate the key features of **Spring Cloud**; specifically service discovery, the configuration server and circuit breaker

Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state). Coordination of distributed systems leads to boiler plate patterns, and using Spring Cloud developers can quickly stand up services and applications that implement those patterns. They will work well in any distributed environment, including the developer's own laptop, bare metal data centres, and managed platforms such as Cloud Foundry.


### How do I get set up? ###

* These instructions assume you are using STS
* Clone https://bitbucket.org/architech/spring-cloud-poc
* Import the **config** folder as a project
* Import all other top-level folders as maven projects
* In the **config-server** project, edit **src/main/resources/application.properties**, setting **spring.cloud.config.server.native.search-locations** to point to the top-level of the config project
* In the 'Boot Dashboard', highlight **config-server** and select '(Re)Start'
* Start the other applications, in the following order: discovery-server, tracing-server, hystrix-dashboard, catalog-service, promotions-service, commerce-website
* You should now be able to view the main website at **http://localhost:8000/**
* try the admin portal at **http://localhost:8000/admin**


### Who do I talk to? ###

* If you have any problems or questions, please don't hesitate to reach out to Steve Kotsopoulos, sk@architech.ca