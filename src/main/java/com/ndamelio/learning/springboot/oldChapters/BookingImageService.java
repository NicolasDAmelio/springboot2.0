package com.ndamelio.learning.springboot.oldChapters;

import com.ndamelio.learning.springboot.images.Image;
import com.ndamelio.learning.springboot.images.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class BookingImageService {

    private final ImageService imageService;

    public BookingImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    public List<Image> findAllImages() {
        return imageService.findAllImages()
                .collectList()
                .block(Duration.ofSeconds(10));
    }

    public Resource findOneImage(String filename) {
        return imageService.findOneImage(filename)
                .block(Duration.ofSeconds(30));
    }

    public void createImage(List<FilePart> files) {
        imageService.createImage(Flux.fromIterable(files))
                .block(Duration.ofMinutes(1));
    }

    public void deleteImage(String filename) {
        imageService.deleteImage(filename)
                .block(Duration.ofSeconds(20));
    }
}
