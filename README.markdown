Follow these steps to get started:

1. Git-clone this repository.

        $ https://github.com/ASRagab/spray-akka-videri-sample my-project

2. Change directory into your clone:

        $ cd my-project

3. Launch SBT:

        $ sbt

4. Compile everything and run all tests:

        > test

5. To run:

        > run
        
6. Send requests via Postman or some other runner

    - POST /metrics
      > sample post body
      ``` javascript
      { 
          "id":2,
          "payload":"tone clone phones strip mosquitoes dogs cats mice and rain",
          "timestamp": 1458853122409
      }
      ```
    
    - GET /metrics
    
    

