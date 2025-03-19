package org.example;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Main {
    public static Set<Mage> getTestData(Supplier<Set<Mage>> setFactory) {
        var set = setFactory.get();
        set.add(new Mage("Mage 1", 5, 3.2, setFactory.get()));
        var mage2Apprentices = setFactory.get();
        var mage23Apprentices = setFactory.get();
        mage23Apprentices.addAll(List.of(new Mage[] {
                new Mage("Mage 2-3-1", 2, 50.0, setFactory.get()),
                new Mage("Mage 2-3-2", 10, 23.0, setFactory.get())
        }));
        mage2Apprentices.addAll(List.of(new Mage[]{
                new Mage("Mage 2-1", 1, 2.0, setFactory.get()),
                new Mage("Mage 2-2", 2, 1.0, setFactory.get()),
                new Mage("Mage 2-3", 4, 2.0, mage23Apprentices)
        }));
        set.add(new Mage("Mage 2", 3, 10.2, mage2Apprentices));
        var mage3Apprentices = setFactory.get();
        var mage32Apprentices = setFactory.get();
        mage32Apprentices.addAll(List.of(new Mage[] {
                new Mage("Mage 3-2-1", 20, 25.0, setFactory.get())
        }));
        mage3Apprentices.addAll(List.of(new Mage[] {
                new Mage("Mage 3-1", 10, 6.0, setFactory.get()),
                new Mage("Mage 3-2", 15, 2.0, mage32Apprentices)
        }));
        set.add(new Mage("Mage 3", 20, 12.0, mage3Apprentices));
        return set;
    }

    public static void printMages(Set<Mage> mages, String prefix) {
        var index = 1;
        for (var mage : mages) {
            System.out.printf("%s\t%s%n", String.format("%s%d.", prefix, index), mage.toString());
            printMages(mage.getApprentices(), String.format("\t%s%d.", prefix, index));
            index++;
        }
    }

    public static void main(String[] args) {
        MageSortingMethod method = MageSortingMethod.NoSorting;
        if (args.length >= 1) {
            if (args[0].equals("natural")) {
                method = MageSortingMethod.NaturalSorting;
            }else if (args[0].equals("alternative")) {
                method = MageSortingMethod.AlternativeSorting;
            }
        }

        Supplier<Set<Mage>> setFactory = method == MageSortingMethod.NoSorting
                ? HashSet::new
                : method == MageSortingMethod.NaturalSorting
                    ? TreeSet::new
                    : () -> new TreeSet<>(new AlternativeMageComparator());

        var testSet = getTestData(setFactory);

        printMages(testSet, "");
        System.out.println();

        var allMages = testSet.stream().flatMap(m -> m.getDescendants().stream()).collect(Collectors.toSet());
        allMages.addAll(testSet);

        var mageDescendants = allMages.stream().collect(Collectors.toMap((mage) -> mage, Mage::getNumberOfDescendants));
        mageDescendants.forEach((key, value) -> System.out.printf("%d\t%s%n", value, key.getName()));
    }
}