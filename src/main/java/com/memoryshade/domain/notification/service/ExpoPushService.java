package com.memoryshade.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpoPushService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestTemplate restTemplate;

    public void sendGuardianLinkRequest(String expoPushToken, String guardianName, Long requestId) {
        if (!StringUtils.hasText(expoPushToken)) {
            return;
        }

        Map<String, Object> payload = Map.of(
                "to", expoPushToken,
                "sound", "default",
                "title", "보호자 연결 요청",
                "body", guardianName + "님이 보호자 연결을 요청했습니다.",
                "data", Map.of(
                        "type", "GUARDIAN_LINK_REQUEST",
                        "requestId", requestId
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.postForEntity(EXPO_PUSH_URL, new HttpEntity<>(payload, headers), String.class);
        } catch (RestClientException e) {
            log.warn("Expo push send failed. requestId={}, token={}", requestId, expoPushToken, e);
        }
    }
}
