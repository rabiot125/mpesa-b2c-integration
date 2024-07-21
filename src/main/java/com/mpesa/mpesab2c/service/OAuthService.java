package com.mpesa.mpesab2c.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.config.MpesaConfiguration;
import com.mpesa.mpesab2c.dtos.OAuthResponseDto;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OAuthService {
    private final ObjectMapper objectMapper;
    private final MpesaConfiguration mpesaConfiguration;
    public OAuthService(ObjectMapper objectMapper, MpesaConfiguration mpesaConfiguration) {
        this.objectMapper = objectMapper;
        this.mpesaConfiguration = mpesaConfiguration;
    }


    public OAuthResponseDto generateToken() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(String.format("%s?grant_type=%s", mpesaConfiguration.getOauthEndpoint(), mpesaConfiguration.getGrantType()))
                .method("GET", null)
                .addHeader("Authorization", "Basic RHJmTE8yNjdMWXhrZkV0MlBZckd3OHJzZ0FMcVVHaWJpNnA4bXNFeE5RTzdLS3JkOjI0Mmk0WHVoR2tIb1JLUXJEaGRwRDZHVXpNR3Y2ZmlQamlxQ0xIQnNXdlk4Nk1laTZDcXFueUdyVE5COFdzSFE=")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                String accessToken = jsonNode.get("access_token").asText();
                String expiresIn = jsonNode.get("expires_in").asText();

                OAuthResponseDto oAuthResponseDto = new OAuthResponseDto();
                oAuthResponseDto.setAccessToken(accessToken);
                oAuthResponseDto.setExpiresIn(expiresIn);
                return oAuthResponseDto;
            } else {
                throw new RuntimeException("Failed to generate token: " + response.message());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate token", e);
        }

    }
}
