package com.ndamelio.learning.springboot;

import lombok.Data;

@Data
public class Image {

    private String id;
    private String name;

    public Image(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
