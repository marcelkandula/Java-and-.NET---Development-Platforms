package org.example;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RepositoryTest {

    private AuthorRepository repo;
    private Author author;

    @BeforeEach
    void setUp() {
        repo = new AuthorRepository();
        author = new Author();
        author.setName("Adam Mickiewicz");
    }

    @Test
    void saveNewAuthor() {
        repo.save(author);
        Optional<Author> result = repo.find(author.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Adam Mickiewicz");
    }

    @Test
    void saveDuplicateThrows() {
        repo.save(author);
        assertThatThrownBy(() -> repo.save(author))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findReturnsEmptyForNonExisting() {
        Optional<Author> result = repo.find(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void deleteExistingAuthor() {
        repo.save(author);
        repo.delete(author.getId());
        assertThat(repo.find(author.getId())).isEmpty();
    }

    @Test
    void deleteNonExistingThrows() {
        assertThatThrownBy(() -> repo.delete(UUID.randomUUID()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
