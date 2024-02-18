package com.mlesniak.shortener;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class LinkController {
    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    public record LinkRequest(String url) {
    }

    public record LinkResponse(String shortUrl) {
    }

    @PostMapping("/")
    public LinkResponse add(@RequestBody LinkRequest request) {
        var shortUrl = linkService.create(new Url(request.url));
        return new LinkResponse(shortUrl.url());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> get(@PathVariable String id) {
        Optional<Url> url = linkService.get(new Id(id));
        return url
                .<ResponseEntity<Void>>map(value ->
                        ResponseEntity
                                .status(HttpStatus.FOUND)
                                .header(HttpHeaders.LOCATION, value.url())
                                .build())
                .orElseGet(() ->
                        ResponseEntity
                                .notFound()
                                .build());
    }
}
