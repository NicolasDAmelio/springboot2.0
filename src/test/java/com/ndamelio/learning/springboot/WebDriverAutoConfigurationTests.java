package com.ndamelio.learning.springboot;

import org.apache.commons.lang3.ClassUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class WebDriverAutoConfigurationTests {

    private AnnotationConfigApplicationContext context;

    @After
    public void close() {
        if (this.context != null) {
            this.context.close();
        }
    }

    private void load(Class<?>[] configs, String... environment) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        applicationContext.register(WebDriverAutoConfiguration.class);

        if (configs.length > 0) {
            applicationContext.register(configs);
        }

        EnvironmentTestUtils.addEnvironment(applicationContext, environment);

        applicationContext.refresh();
        this.context = applicationContext;
    }

    @Test
    public void fallbackToNonGuiModeWhenAllBrowsersDisabled() {
        load(new Class[]{},
                "com.ndamelio.webdriver.firefox.enabled:false",
                "com.ndamelio.webdriver.safari.enabled:false",
                "com.ndamelio.webdriver.chrome.enabled:false");
        WebDriver driver = context.getBean(WebDriver.class);
        Assertions.assertThat(ClassUtils.isAssignable(TakesScreenshot.class, driver.getClass())).isFalse();
        Assertions.assertThat(ClassUtils.isAssignable(HtmlUnitDriver.class, driver.getClass())).isTrue();
    }

    @Test
    public void testWithMockedFirefox() {
        load(new Class[]{MockFirefoxConfiguration.class},
                "com.ndamelio.webdriver.safari.enabled:false",
                "com.ndamelio.webdriver.chrome.enabled:false");

        WebDriver driver = context.getBean(WebDriver.class);
        Assertions.assertThat(ClassUtils.isAssignable(TakesScreenshot.class,driver.getClass())).isTrue();
        Assertions.assertThat(ClassUtils.isAssignable(FirefoxDriver.class, driver.getClass())).isTrue();
    }

    @Configuration
    protected static class MockFirefoxConfiguration {
        @Bean
        FirefoxDriverFactory firefoxDriverFactory() {
            FirefoxDriverFactory factory = Mockito.mock(FirefoxDriverFactory.class);
            BDDMockito.given(factory.getObject())
                    .willReturn(Mockito.mock(FirefoxDriver.class));

            return factory;g
        }
    }

}
