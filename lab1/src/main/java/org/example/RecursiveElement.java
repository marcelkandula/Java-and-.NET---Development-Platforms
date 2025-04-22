package org.example;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

public class RecursiveElement implements Comparable<RecursiveElement> {
    private String name;
    public int value;
    private double weight;
    private Set<RecursiveElement> children;

    public RecursiveElement(String name, int value, double weight) {
        this.name = name;
        this.value = value;
        this.weight = weight;
        this.children = new HashSet<>();
    }

    public void addChild(RecursiveElement child) {
        this.children.add(child);
    }

    public Set<RecursiveElement> getChildren() {
        return children;
    }

    @Override
    public int compareTo(RecursiveElement other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RecursiveElement that = (RecursiveElement) obj;
        return value == that.value &&
                Double.compare(that.weight, weight) == 0 &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, weight);
    }

    @Override
    public String toString() {
        return "RecursiveElement{name='" + name + "', value=" + value + ", weight=" + weight + ", children=" + children.size() + "}";
    }
}