package com.ndamelio.learning.springboot.comments;

import org.springframework.data.repository.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommentWriterRepository extends Repository<Comment, String> {
    Flux<Comment> saveAll(Flux<Comment> newComments);

    Mono<Comment> save(Comment newComment);

    // Needed to support save()
    Mono<Comment> findById(String id);
}
