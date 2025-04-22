package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Entity
public class Book {
    @Id
    public UUID id = UUID.randomUUID();

    @Setter @Getter
    private String Title;

    @Setter @Getter
    @ManyToOne
    private Author Author;
}
