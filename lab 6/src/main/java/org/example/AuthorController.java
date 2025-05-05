package org.example;

import java.util.UUID;

public class AuthorController {
    private final AuthorRepository repo;

    public AuthorController(AuthorRepository repo) {
        this.repo = repo;
    }

    public String save(String input) {
        AuthorDto dto = new AuthorDto();
        dto.name = input;
        Author a = dto.toEntity();
        try {
            repo.save(a);
            return "done";
        } catch (IllegalArgumentException e) {
            return "bad request";
        }
    }

    public String delete(String id) {
        try {
            repo.delete(UUID.fromString(id));
            return "done";
        } catch (IllegalArgumentException e) {
            return "not found";
        }
    }

    public String find(String id) {
        return repo.find(UUID.fromString(id))
                .map(AuthorDto::from)
                .map(dto -> dto.id + ":" + dto.name)
                .orElse("not found");
    }
}