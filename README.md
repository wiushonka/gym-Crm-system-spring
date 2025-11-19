This is springboot app for gym CRM system.

All homeworks tasks given in .pdf-s are merged together and form full application.

initially Command Line Runner will load some data in db tables, but only if path is correct. 
currently path is set correctly for my computer, so in order for it to work:
            
            1)whole correct path of each *_Data.txt* or any external file where initial information
            is loaded form, must be provided in *resources/application.properties* .
            correct setup for some application properties should look like this : 
                        
                        trainer.data.file=*full path to your file*
                        trainee.data.file=*full path to your file*
                        trainingTypes.data.file=*full path to your file*
            
            2)Formatting must be correct for each file, otherwise it will throw 
            parsing errors and fail.

For testing h2 scheme is used, and test profile properties are set in test\resources

Make sure to choose suitable active profile in application.properties and set up db and file paths in 
that profile. Separate Microservice (Trainer Workload) is used to aggregate some data whenever training is 
added or deleted. Asynchronous messaging is managed by ActiveMQ artemis. When testing with Cucumber make sure
that message broker is active, since cucumber integration tests of gymCrmSystem also boots up and waits for 
workload service with test profile and when testing it is important to have active message broker. 
Also I dont know why but on github it shows 80% of code as html and there is actually only java and Gherkin 
and only 2 small thymeleaf teamplates. 
