package co.com.reactive.consumer.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class RestConsumerConfigTest {

    private RestConsumerConfig restConsumerConfig;

    @BeforeEach
    void setUp() {
        String testUrl = "http://localhost:8080";
        int timeout = 5000;
        restConsumerConfig = new RestConsumerConfig(testUrl, timeout);
    }

    @Test
    void shouldCreateWebClientWithoutErrors() {
        WebClient webClient = restConsumerConfig.getWebClient(WebClient.builder());

        assertThat(webClient).isNotNull();

        WebClient.RequestHeadersSpec<?> request = webClient.get().uri("/");
        assertThat(request).isNotNull();
    }
}