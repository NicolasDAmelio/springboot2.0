package com.ndamelio.learning.springboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InitDatabase {

    private static final Logger LOG = LoggerFactory.getLogger(InitDatabase.class);

    @Bean
    CommandLineRunner init(MongoOperations operations) {
        return args -> {
            operations.dropCollection(Employee.class);
            operations.dropCollection(Image.class);

            Employee e1 = new Employee();
            e1.setId(UUID.randomUUID().toString());
            e1.setFirstName("Bilbo");
            e1.setLastName("Baggins");
            e1.setRole("burglar");
            operations.insert(e1);

            Employee e2 = new Employee();
            e2.setId(UUID.randomUUID().toString());
            e2.setFirstName("Frodo");
            e2.setLastName("Baggins");
            e2.setRole("ring bearer");
            operations.insert(e2);

            operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
            operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
            operations.insert(new Image("3", "bazinga.png"));

            operations.findAll(Image.class).forEach(image -> LOG.info(image.toString()));
        };
    }
}
