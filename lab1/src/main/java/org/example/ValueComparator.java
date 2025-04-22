package org.example;

import java.util.Comparator;

public class ValueComparator implements Comparator<RecursiveElement> {
    @Override
    public int compare(RecursiveElement e1, RecursiveElement e2){
        return Integer.compare(e1.value, e2.value);
    }
}
