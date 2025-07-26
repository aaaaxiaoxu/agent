package com.adx.agent.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;


@RestController
public class ResearchChatController {
    private static final String DIFY_URL = "https://api.dify.ai/v1/chat-messages";
    private static final String API_KEY = "app-gotk0ccpLfDgxQE7AVD3Ua9z";

    @PostMapping("/chat_research")
    public ResponseEntity<String> chat(@RequestBody Map<String, Object> payload) {
        try {
            String userMsg = (String) payload.get("message");
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);
            Map<String, Object> body = new HashMap<>();
            body.put("inputs", new HashMap<>());
            body.put("query", userMsg);
            body.put("response_mode", "streaming");
            body.put("user", "test-user");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(DIFY_URL, request, String.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("后端异常: " + e.getMessage());
        }
    }
} 