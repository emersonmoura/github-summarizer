# README #

### To run it ###

Have in mind that you must have a Scala environment

* Scala version 2.12.8
* SBT version 1.3.8

execute the command
```
sbt run
```

or executing the main class
```
scalac.summarizer.Main
```

after that, you will be able to make requests
```
curl localhost:8080/org/{org_name}/contributors
```

### Contextualization ###

GitHub portal is centered around organizations and repositories. 
Each organization has many repositories and each repository has many contributors. 

This system has an endpoint that given the name of the organization will return a list of contributors sorted by the
number of contributions.