package org.example;

import java.util.UUID;


public class Author {

    private UUID id = UUID.randomUUID();

    private String Name;


    public UUID getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
