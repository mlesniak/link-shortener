package com.mlesniak.shortener;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryLinkRepository implements LinkRepository {
    private final Map<String, String> links = new HashMap<>();

    @Override
    public Optional<Url> get(Id id) {
        var url = links.get(id.id());
        return Optional.ofNullable(url).map(Url::new);
    }

    @Override
    public void save(Id id, Url url) {
        links.put(id.id(), url.url());
    }
}
