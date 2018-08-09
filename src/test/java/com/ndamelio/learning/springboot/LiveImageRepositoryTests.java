package com.ndamelio.learning.springboot;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class LiveImageRepositoryTests {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    MongoOperations operations;

    @Test
    public void findAllShouldWork() {
        Flux<Image> images = imageRepository.findAll();

        StepVerifier.create(images)
                .recordWith(ArrayList::new)
                .expectNextCount(3)
                .consumeRecordedWith(results -> {
                    Assertions.assertThat(results).hasSize(3);
                    Assertions.assertThat(results)
                            .extracting(Image::getName)
                            .contains(
                                    "learning-spring-boot-cover.jpg",
                                    "learning-spring-boot-2nd-edition-cover.jpg",
                                    "bazinga.png"
                            );
                })
                .expectComplete()
                .verify();
    }

    @Test
    public void findByNameShouldWork() {
        Mono<Image> image = imageRepository.findByName("bazinga.png");
        StepVerifier.create(image)
                .expectNextMatches(results -> {
                    Assertions.assertThat(results.getName()).isEqualTo("bazinga.png");
                    Assertions.assertThat(results.getId()).isEqualTo("3");
                    return true;
                });
    }
}
