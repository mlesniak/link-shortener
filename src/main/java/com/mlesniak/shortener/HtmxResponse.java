package com.mlesniak.shortener;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

// Domain wrapper for a combined response.
public class HtmxResponse extends ResponseEntity<String> {
    public HtmxResponse(String body, MultiValueMap<String, String> headers, HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }
}
