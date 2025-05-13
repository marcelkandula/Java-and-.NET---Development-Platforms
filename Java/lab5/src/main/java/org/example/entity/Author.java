package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;

@NoArgsConstructor
@Entity
public class Author {
    @Getter
    @Id
    private UUID id = UUID.randomUUID();

    @Getter @Setter
    private String Name;

    @Getter
    @OneToMany(mappedBy = "Author", cascade = CascadeType.ALL)
    private List<Book> Books = new ArrayList<>();
}
