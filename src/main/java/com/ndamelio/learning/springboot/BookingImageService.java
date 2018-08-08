package com.ndamelio.learning.springboot;

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


}
