package org.example;

public class AuthorDto {
    public String id;
    public String name;

    public static AuthorDto from(Author a) {
        AuthorDto dto = new AuthorDto();
        dto.id = a.getId().toString();
        dto.name = a.getName();
        return dto;
    }

    public Author toEntity() {
        Author a = new Author();
        a.setName(name);
        return a;
    }
}