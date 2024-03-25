package com.mlesniak.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
public class LinkService {
    private static final Logger log = LoggerFactory.getLogger(LinkService.class);
    LinkRepository repository;

    @Value("${hostname}")
    private String hostname;

    public LinkService(LinkRepository repository) {
        this.repository = repository;
    }

    public Optional<Url> get(Id id) {
        log.info("get({})", id);
        return repository.get(id);
    }

    // @mlesniak DoS Protection: if we store more than 1k links, do not create one.
    public Url create(Url url) {
        String id = null;
        var sha = getSHA256(url.url());
        log.debug("SHA={} for {}", sha, url.url());

        int length = 1;
        while (length < sha.length()) {
            var tmpId = sha.substring(0, length);
            var tmpUrl = repository.get(new Id(tmpId));
            if (tmpUrl.isEmpty()) {
                id = tmpId;
                break;
            }
            if (tmpUrl.get().url().equalsIgnoreCase(url.url())) {
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

        var fixedUrl = url;
        if (!url.url().startsWith("http")) {
            // We could use https, but not every website provides
            // an https endpoint and the good ones redirect from
            // http to https anyway.
            fixedUrl = new Url("http://" + url.url());
        }

        repository.save(new Id(id), fixedUrl);
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
