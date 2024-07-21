package com.mpesa.mpesab2c.controller;

import com.mpesa.mpesab2c.dtos.OAuthResponseDto;
import com.mpesa.mpesab2c.service.OAuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Authorization")
public class OAuthController {
    private final OAuthService authService;

    public OAuthController(OAuthService authService) {
        this.authService = authService;
    }
@Operation(summary = "Generate Auth Token")
    @GetMapping(value = "/oauth/generate-token",produces = "application/json")
    public ResponseEntity<OAuthResponseDto> generateToken() {
        OAuthResponseDto token = authService.generateToken();
        return ResponseEntity.ok(token);
    }
}
