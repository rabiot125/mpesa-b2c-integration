package com.mpesa.mpesab2c.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpesa.mpesab2c.config.MpesaConfiguration;
import com.mpesa.mpesab2c.dtos.OAuthResponseDto;
import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)

public class OAuthServiceTest {
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private MpesaConfiguration mpesaConfiguration;
    @InjectMocks
    private OAuthService oAuthService;
    @Mock
    private OkHttpClient client;
    @Mock
    private Response response;
    @Mock
    private ResponseBody responseBody;

    private static final String OAUTH_ENDPOINT = "https://sandbox.safaricom.co.ke/oauth/v1/generate";
    private static final String GRANT_TYPE = "client_credentials";
    private static final String ACCESS_TOKEN = "Basic";
    private static final String EXPIRES_IN = "3599";
    private static final String RESPONSE_JSON = "{\"access_token\": \"" + ACCESS_TOKEN + "\", \"expires_in\": \"" + EXPIRES_IN + "\"}";

    @BeforeEach
    void setUp() {

        when(mpesaConfiguration.getOauthEndpoint()).thenReturn(OAUTH_ENDPOINT);
        when(mpesaConfiguration.getGrantType()).thenReturn(GRANT_TYPE);
    }

    @Test
    void generateToken_successful() throws IOException {

        // Setup

        when(client.newCall(any(Request.class)).execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(true);
        when(response.body()).thenReturn(responseBody);
        when(responseBody.string()).thenReturn(RESPONSE_JSON);

        JsonNode jsonNode = mock(JsonNode.class);
        when(objectMapper.readTree(RESPONSE_JSON)).thenReturn(jsonNode);
        when(jsonNode.get("access_token")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("access_token").asText()).thenReturn(ACCESS_TOKEN);
        when(jsonNode.get("expires_in")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("expires_in").asText()).thenReturn(EXPIRES_IN);

        // Execute
        OAuthResponseDto result = oAuthService.generateToken();

        // Verify

        assertNotNull(result);
        assertEquals(ACCESS_TOKEN, result.getAccessToken());
        assertEquals(EXPIRES_IN, result.getExpiresIn());

        verify(client).newCall(any(Request.class));
        verify(response).isSuccessful();
        verify(responseBody).string();
    }

    @Test
    void generateToken_failedResponse() throws IOException {
        // Setup
        when(client.newCall(any(Request.class)).execute()).thenReturn(response);
        when(response.isSuccessful()).thenReturn(false);
        when(response.message()).thenReturn("Unauthorized");

        // Execute & Verify
        RuntimeException exception = assertThrows(RuntimeException.class, () -> oAuthService.generateToken());
        assertEquals("Failed to generate token: Unauthorized", exception.getMessage());

        verify(client).newCall(any(Request.class));
        verify(response).isSuccessful();
    }

    @Test
    void generateToken_ioException() throws IOException {
        // Setup
        when(client.newCall(any(Request.class)).execute()).thenThrow(new IOException("Network error"));

        // Execute & Verify
        RuntimeException exception = assertThrows(RuntimeException.class, () -> oAuthService.generateToken());
        assertEquals("Failed to generate token", exception.getMessage());
        assertTrue(exception.getCause() instanceof IOException);

        verify(client).newCall(any(Request.class));
    }
}

