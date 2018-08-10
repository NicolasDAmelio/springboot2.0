package com.ndamelio.learning.springboot;

import com.ndamelio.learning.springboot.images.Image;
import com.ndamelio.learning.springboot.images.ImageService;
import com.ndamelio.learning.springboot.oldChapters.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ThymeleafAutoConfiguration.class})
public class HomeControllerTests {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ImageService imageService;

    @MockBean
    EmployeeRepository employeeRepository;

    @MockBean
    ReactiveMongoOperations operations;

    @Test
    public void baseRouteShouldListAllImages() {
        // given
        Image alphaImage = new Image("1", "alpha.png");
        Image bravoImage = new Image("2", "bravo.png");

        BDDMockito.given(imageService.findAllImages())
                .willReturn(Flux.just(alphaImage, bravoImage));

        // when
        EntityExchangeResult<String> result = webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult();

        // then
        Mockito.verify(imageService).findAllImages();
        Mockito.verifyNoMoreInteractions(imageService);
        Assertions.assertThat(result.getResponseBody())
                .contains("<title>Learning Spring Boot: Spring-a-Gram</title>")
                .contains("<a href=\"/images/alpha.png/raw\">")
                .contains("<a href=\"/images/bravo.png/raw\">");
    }

    @Test
    public void fetchingImageShouldWork() {
        BDDMockito.given(imageService.findOneImage(ArgumentMatchers.any()))
                .willReturn(Mono.just(
                        new ByteArrayResource("data".getBytes())));

        webTestClient.get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("data");

        Mockito.verify(imageService).findOneImage("alpha.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }

    @Test
    public void fetchingNullImageShouldFail() throws IOException {
        Resource resource = Mockito.mock(Resource.class);
        BDDMockito.given(resource.getInputStream())
                .willThrow(new IOException("Bad file"));
        BDDMockito.given(imageService.findOneImage(ArgumentMatchers.any()))
                .willReturn(Mono.just(resource));

        webTestClient
                .get().uri("/images/alpha.png/raw")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("Couldn't find alpha.png => Bad file");

        Mockito.verify(imageService).findOneImage("alpha.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }

    @Test
    public void deleteImageShouldWork() {
        Image alphaImage = new Image("1", "alpha.png");
        BDDMockito.given(imageService.deleteImage(ArgumentMatchers.any())).willReturn(Mono.empty());

        webTestClient
                .delete().uri("/images/alpha.png")
                .exchange()
                .expectStatus().isSeeOther()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/");

        Mockito.verify(imageService).deleteImage("alpha.png");
        Mockito.verifyNoMoreInteractions(imageService);
    }
}