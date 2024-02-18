package com.mlesniak.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class LinkService {
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);
    private final Map<String, String> links = new HashMap<>();

    @Value("${hostname}")
    private String hostname;

    public Optional<Url> get(Id id) {
        var url = links.get(id.id());
        return Optional.ofNullable(url).map(Url::new);
    }

    public Url create(Url url) {
        String id = null;
        var sha = getSHA256(url.url());
        log.debug("SHA={} for {}", sha, url.url());

        int length = 1;
        while (length < sha.length()) {
            var tmpId = sha.substring(0, length);
            var tmpUrl = links.get(tmpId);
            if (tmpUrl == null) {
                id = tmpId;
                break;
            }
            if (tmpUrl.equalsIgnoreCase(url.url())) {
                id = tmpId;
                break;
            }

            // Collision happened, increase length.
            length++;
        }
        if (id == null) {
            // @mlesniak Add general error handler.
            // @mlesniak Add general comment why this can't happen.
        }

        links.put(id, url.url());
        var shortUrl = "%s/%s".formatted(hostname, id);
        return new Url(shortUrl);
    }

    private String getSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
