package org.example;

import java.util.Comparator;

public class ValueComparator implements Comparator<com.example.RecursiveElement> {
    @Override
    public int compare(com.example.RecursiveElement e1, com.example.RecursiveElement e2){
        return Integer.compare(e1.value, e2.value);
    }
}
