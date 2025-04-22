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
        RecursiveElement child3 = new RecursiveElement("Child3", 31, 1.8);

        RecursiveElement subchild = new RecursiveElement("subchild", 10, 0.9);
        RecursiveElement subchild2 = new RecursiveElement("subchild2", 11, 0.9);


        root.addChild(child1);
        root.addChild(child2);
        root.addChild(child3);
        child1.addChild(subchild);
        child3.addChild(subchild2);

        elements.add(root);
        elements.add(child1);
        elements.add(child2);
        elements.add(child3);
        elements.add(subchild);
        elements.add(subchild2);

        printElements(elements, 0);


        boolean sorted = sortType.equals("natural") || sortType.equals("comparator");
        Map<RecursiveElement, Integer> stats = generateStatistics(elements, sorted);
        System.out.println("\nStatystyki elementów podrzędnych:");
        for (Map.Entry<RecursiveElement, Integer> entry : stats.entrySet()) {
            System.out.println(entry.getKey().toString() + " -> " + entry.getValue());
        }
    }

    private static void printElements(Set<RecursiveElement> elements, int level) {
        String indent = "  ".repeat(level);
        for (RecursiveElement element : elements) {
            System.out.println(indent + element.toString());
            printElements(element.getChildren(), level + 1);
        }
    }

    private static int countDescendants(RecursiveElement element) {
        int count = element.getChildren().size();
        for (RecursiveElement child : element.getChildren()) {
            count += countDescendants(child);
        }
        return count;
    }

    private static Map<RecursiveElement, Integer> generateStatistics(Set<RecursiveElement> elements, boolean sorted) {
        Map<RecursiveElement, Integer> stats;
        if (sorted) {
            stats = new TreeMap<>();
        } else {
            stats = new HashMap<>();
        }
        for (RecursiveElement element : elements) {
            stats.put(element, countDescendants(element));
        }
        return stats;
    }


}