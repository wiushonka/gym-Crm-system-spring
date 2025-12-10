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
added or deleted. Asynchronous messaging is managed by ActiveMQ artemis. Included in Docker. 

Since containers must be totally independent I could not use same classes from different Microservices from other 
Microservices. So now I have same Type of Record in both Services and communication works via Maps which get 
converted and checked by Services.

Since docker can start up both Microservices and ActiveMQ test should be run from docker, otherwise some test
which require Integration will fail. Or all Services and Queue activated. During tests Main service no longer
can boot up microservice since they must be divided to containerise. 

*Also I dont know why but on github it shows 80% of code as html and there is actually only java and Gherkin 
and only 2 small thymeleaf teamplates.* 
