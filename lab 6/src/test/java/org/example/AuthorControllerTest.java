package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

public class AuthorControllerTest {

    AuthorRepository repo;
    AuthorController controller;

    @BeforeEach
    void setup() {
        repo = mock(AuthorRepository.class);
        controller = new AuthorController(repo);
    }

    @Test
    void testSaveDone() {
        assertEquals("done", controller.save("Jan Nowak"));
        verify(repo).save(any());
    }

    @Test
    void testSaveDuplicate() {
        doThrow(new IllegalArgumentException()).when(repo).save(any());
        assertEquals("bad request", controller.save("Jan Nowak"));
    }

    @Test
    void testDeleteDone() {
        assertEquals("done", controller.delete(UUID.randomUUID().toString()));
    }

    @Test
    void testDeleteFail() {
        doThrow(new IllegalArgumentException()).when(repo).delete(any());
        assertEquals("not found", controller.delete(UUID.randomUUID().toString()));
    }

    @Test
    void testFindNotFound() {
        when(repo.find(any())).thenReturn(Optional.empty());
        assertEquals("not found", controller.find(UUID.randomUUID().toString()));
    }
}