package fpt.edu.vn.gms.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class SmsServiceViettelImpl {

    @Value("${sms.viettel.api-url}")
    private String apiUrl;

    @Value("${sms.viettel.user-id}")
    private String userId;

    @Value("${sms.viettel.service-id}")
    private String serviceId;

    @Value("${sms.viettel.sender}")
    private String sender;

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendSms(String phone, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "User_ID", userId,
                "Service_ID", serviceId,
                "Receiver_ID", phone,
                "Sender_ID", sender,
                "Message", message
        );

        ResponseEntity<String> res = restTemplate.postForEntity(apiUrl, new HttpEntity<>(payload, headers), String.class);

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Send SMS failed: " + res.getBody());
        }
    }
}
