package com.mlesniak.shortener;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LinkController {
    private Map<String, String> links = new HashMap<>();

    public record CreateLinkRequest(String url) {

    }

    public record CreateLinkResponse(String id, String url, String shortUrl) {

    }

    // @mlesniak idemptotency
    @PostMapping("/api/link")
    public CreateLinkResponse add(@RequestBody CreateLinkRequest request) {
        String id = null;
        var sha = getSHA256(request.url);

        int length = 1;
        while (length < sha.length()) {
            var tmpId = sha.substring(0, length);
            var url = links.get(tmpId);
            if (url == null) {
                id = tmpId;
                break;
            }
            if (url.equalsIgnoreCase(request.url)) {
                id = tmpId;
                break;
            }

            // Collision happened, increase length.
            length++;
        }
        if (id == null) {
            // @mlesniak I don't think this can happen.
        }

        links.put(id, request.url);
        return new CreateLinkResponse(id, request.url, "http://localhost:8080/" + id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> get(@PathVariable String id) {
        var url = links.get(id);
        if (url == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, url)
                .build();
    }

    private String getSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
