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
that profile. For MongoDb drops old summarys, because now it is scheduled to aggregate data in every
10 seconds instead of 4 weeks, in comment or delete mongo configuration and uncomment part in MongoSummaryService
which makes it trigger in every 4 weeks.
