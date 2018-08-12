package com.ndamelio.learning.springboot;

import com.ndamelio.learning.springboot.comments.CommentReaderRepository;
import com.ndamelio.learning.springboot.images.ImageService;
import com.ndamelio.learning.springboot.oldChapters.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;

@Controller
public class HomeController {

    private static final String BASE_PATH = "/images";
    private static final String FILENAME = "{filename:.+}";

    private final ImageService imageService;
    private final CommentReaderRepository commentReaderRepository;
//    private final EmployeeRepository employeeRepository;

    @Autowired
    ReactiveMongoOperations operations;

    public HomeController(ImageService imageService, CommentReaderRepository commentReaderRepository) {
//    public HomeController(ImageService imageService, EmployeeRepository employeeRepository) {
        this.imageService = imageService;
        this.commentReaderRepository = commentReaderRepository;
//        this.employeeRepository = employeeRepository;
    }

    @GetMapping(value = BASE_PATH + "/" + FILENAME + "/raw", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public Mono<ResponseEntity<?>> oneRawImage(@PathVariable String filename) {
        return imageService.findOneImage(filename)
                .map(resource -> {
                    try {
                        return ResponseEntity.ok()
                                .contentLength(resource.contentLength())
                                .body(new InputStreamResource(resource.getInputStream()));
                    } catch (IOException e) {
                        return ResponseEntity.badRequest()
                                .body("Couldn't find " + filename + " => " + e.getMessage());
                    }
                });
    }

    @PostMapping(value = BASE_PATH)
    public Mono<String> createFile(@RequestPart(name = "file") Flux<FilePart> files) {
        return imageService.createImage(files).then(Mono.just("redirect:/"));
    }

    @DeleteMapping(BASE_PATH + "/" + FILENAME)
    public Mono<String> deleteFile(@PathVariable String filename) {
        return imageService.deleteImage(filename).then(Mono.just("redirect:/"));
    }

    @GetMapping("/")
    public Mono<String> index(Model model) {
        model.addAttribute("images",
                imageService.findAllImages()
                .flatMap(image -> Mono.just(image).zipWith(commentReaderRepository.findByImageId(image.getId()).collectList()))
                .map(imageAndComments -> new HashMap<String, Object>(){{
                    put("id", imageAndComments.getT1().getId());
                    put("name", imageAndComments.getT1().getName());
                    put("comments", imageAndComments.getT2());
                }}));
        model.addAttribute("extra", "DevTools can also detect code changes too.");
        return Mono.just("index");
    }

//    @GetMapping("/example")
//    @ResponseBody
//    public Flux<Employee> testExample() {
//        Employee e = new Employee();
//        e.setLastName("baggi");
//
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withIgnoreCase()
//                .withMatcher("lastName", ExampleMatcher.GenericPropertyMatchers.startsWith())
//                .withIncludeNullValues();
//
//        Example<Employee> example = Example.of(e, matcher);
//        Flux<Employee> singleEmployee = employeeRepository.findAll(example);
//        return singleEmployee;
//    }

    @GetMapping("/operations")
    @ResponseBody
    public Mono<Employee> operations() {
        Employee e = new Employee();
        e.setFirstName("bilbo");

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withMatcher("firstName", ExampleMatcher.GenericPropertyMatchers.startsWith())
                .withIncludeNullValues();

        Example<Employee> example = Example.of(e, matcher);

        Mono<Employee> singleEmployee = operations.findOne(
                Query.query(Criteria.where("firstName").is("Frodo")), Employee.class);
        return singleEmployee;
    }
}
