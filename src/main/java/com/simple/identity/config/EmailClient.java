package com.simple.identity.config;

import com.simple.identity.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class EmailClient {

    private final ObjectMapper objectMapper;

    @Value("${email.url}")
    private String emailUrl;

    @Value("${email.client-id}")
    private String clientId;

    @Value("${email.client-secret}")
    private String clientSecret;

    private final WebClient webClient = WebClient.builder().build();

    public String sendEmail(EmailRequest emailRequest) {
        String emailJson = objectMapper.writeValueAsString(emailRequest);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("email", emailJson)
                .contentType(MediaType.APPLICATION_JSON);

        return webClient.post()
                .uri(emailUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("X-CLIENT-ID", clientId)
                .header("X-CLIENT-SECRET", clientSecret)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
