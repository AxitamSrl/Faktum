package com.faktum.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
            "sub", jwt.getSubject(),
            "name", jwt.getClaimAsString("name") != null ? jwt.getClaimAsString("name") : "",
            "email", jwt.getClaimAsString("email") != null ? jwt.getClaimAsString("email") : "",
            "groups", jwt.getClaimAsStringList("groups") != null ? jwt.getClaimAsStringList("groups") : List.of()
        );
    }
}
