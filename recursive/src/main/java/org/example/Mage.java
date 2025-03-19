package org.example;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Mage implements Comparable<Mage> {
    public Mage(String name, int level, double power, Set<Mage> apprentices) {
        this.name = name;
        this.level = level;
        this.power = power;
        this.apprentices = apprentices;
    }

    private final String name;
    private final int level;
    private final double power;
    private final Set<Mage> apprentices;

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public double getPower() {
        return power;
    }

    public Set<Mage> getApprentices() {
        return apprentices;
    }

    public int getNumberOfDescendants() {
        return apprentices.size() + apprentices.stream().mapToInt(Mage::getNumberOfDescendants).sum();
    }

    public Set<Mage> getDescendants() {
        var set = apprentices.stream().flatMap(m -> m.getDescendants().stream()).collect(Collectors.toSet());
        set.addAll(apprentices);
        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mage mage = (Mage) o;
        return level == mage.level
                && Double.compare(mage.power, power) == 0
                && name.equals(mage.name)
                && apprentices.equals(mage.apprentices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, level, power, apprentices);
    }

    @Override
    public String toString() {
        return String.format("Mage{name='%s', level=%d, power=%f}", name, level, power);
    }

    @Override
    public int compareTo(Mage o) {
        var order = name.compareTo(o.name);
        if (order != 0) {
            return order;
        }

        order =  Integer.compare(level, o.level);
        if (order != 0) {
            return order;
        }

        order = Double.compare(power, o.power);
        if (order != 0) {
            return order;
        }

        return 0;
    }
}
