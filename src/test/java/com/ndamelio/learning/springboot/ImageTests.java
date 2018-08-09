package com.ndamelio.learning.springboot;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ImageTests {

    @Test
    public void imagesManagedByLombokShouldWork() {
        Image image = new Image("id", "file-name.jpg");
        image.setId("id");
        image.setName("file-name.jpg");
        image.toString();
        Assertions.assertThat(image.getId()).isEqualTo("id");
        Assertions.assertThat(image.getName()).isEqualTo("file-name.jpg");
    }
}
