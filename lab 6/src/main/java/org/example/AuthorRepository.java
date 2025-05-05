package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AuthorRepository {
    private final Map<UUID, Author> authors = new HashMap<>();

    public void save(Author a) {
        if (authors.containsKey(a.getId())) throw new IllegalArgumentException();
        authors.put(a.getId(), a);
    }

    public void delete(UUID id) {
        if (!authors.containsKey(id)) throw new IllegalArgumentException();
        authors.remove(id);
    }

    public Optional<Author> find(UUID id) {
        return Optional.ofNullable(authors.get(id));
    }
}