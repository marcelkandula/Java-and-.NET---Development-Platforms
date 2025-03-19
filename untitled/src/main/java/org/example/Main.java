package org.example;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        String sortType = args[0];
        Set<RecursiveElement> elements;
        if (sortType.equals("natural")) {
            elements = new TreeSet<>();
        }
        else if (sortType.equals("comparator")){
            elements = new TreeSet<>(new ValueComparator());
        }
        else { elements = new HashSet<>();}

        RecursiveElement root = new RecursiveElement("Root", 50, 1.5);
        RecursiveElement child1 = new RecursiveElement("Child1", 20, 2.1);
        RecursiveElement child2 = new RecursiveElement("Child2", 30, 1.8);
        RecursiveElement subchild = new RecursiveElement("subchild", 10, 0.9);


        root.addChild(child1);
        root.addChild(child2);
        child1.addChild(subchild);
        elements.add(root);
        elements.add(child1);
        elements.add(child2);
        elements.add(subchild);

        printElements(elements);
    }

    private static void printElements(Set<RecursiveElement>elements){
        for (RecursiveElement element : elements){
            System.out.println(element.toString());
            printElements(element.getChildren());
        }
    }
}