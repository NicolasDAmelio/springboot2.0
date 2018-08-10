package com.ndamelio.learning.springboot.comments;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private CommentWriterRepository commentWriterRepository;

    private final MeterRegistry meterRegistry;

    public CommentService(CommentWriterRepository commentWriterRepository, MeterRegistry meterRegistry) {
        this.commentWriterRepository = commentWriterRepository;
        this.meterRegistry = meterRegistry;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = "learning-spring-boot"),
            key = "comments.new"
    ))
    public void save(Comment newComment) {
        commentWriterRepository.save(newComment)
                .log("commentService-save")
                .subscribe(comment -> {
                    meterRegistry
                            .counter("comments.consumed", "imageId", comment.getImageId())
                            .increment();
                });
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    CommandLineRunner setUp(MongoOperations operations) {
        return args -> operations.dropCollection(Comment.class);
    }
}
