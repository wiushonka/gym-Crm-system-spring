package org.example.trainerworkloadservice.config;

import org.example.trainerworkloadservice.model.TrainerSummaryMongo;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoDbConfiguration {
   /* TODO:
            This is only for testing, delete this class or comment this bean out, because each time
            this program starts , this code drops all of trainer_summary data, because for testing
            i create data in every 5 seconds instead of weeks , so i drop them like in actual rel db
            local properties use drop-create and in prod its changed to update.
    */
    @Bean
    public ApplicationRunner init(MongoTemplate mongoTemplate) {
        return args -> {
            mongoTemplate.dropCollection(TrainerSummaryMongo.class);
        };
    }
}
