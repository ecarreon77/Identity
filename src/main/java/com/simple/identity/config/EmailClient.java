package com.simple.identity.config;

import com.simple.identity.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class EmailClient {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String sendEmail(EmailRequest emailRequest) {
        String emailJson = objectMapper.writeValueAsString(emailRequest);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("email", emailJson)
                .contentType(MediaType.APPLICATION_JSON);

        return webClient.post()
                .uri("/api/email/send")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
