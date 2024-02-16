package com.mlesniak.shortener;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LinkController {
    public record CreateLinkRequest(String url) {

    }

    public record CreateLinkResponse(String id, String url, String shortUrl) {

    }


    @PostMapping("/api/link")
    public CreateLinkResponse add(@RequestBody CreateLinkRequest request) {
        String id = "12345";
        return new CreateLinkResponse(id, request.url, request.url);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> get(@PathVariable String id) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, id)
                .build();
    }
}
