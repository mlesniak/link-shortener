package com.mlesniak.shortener;

import java.util.Optional;

public interface LinkRepository {
    Optional<Url> get(Id id);

    void save(Id id, Url url);
}
