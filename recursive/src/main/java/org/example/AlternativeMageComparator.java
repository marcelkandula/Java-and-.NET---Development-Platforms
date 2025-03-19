package org.example;

import java.util.Comparator;

public class AlternativeMageComparator implements Comparator<Mage> {
    @Override
    public int compare(Mage o1, Mage o2) {
        var order = Integer.compare(o1.getLevel(), o2.getLevel());
        if (order != 0) {
            return order;
        }

        order = o1.getName().compareTo(o2.getName());
        if (order != 0) {
            return order;
        }

        order = Double.compare(o1.getPower(), o2.getPower());
        if (order != 0) {
            return order;
        }

        return 0;
    }
}
