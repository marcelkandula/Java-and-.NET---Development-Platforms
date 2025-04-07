package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int numThreads = 2;
        if(args.length > 0) {
            try {
                numThreads = Integer.parseInt(args[0]);
            } catch(NumberFormatException e) {
                System.out.println("Niepoprawny parametr, używam wartości domyślnej: 2.");
            }
        } else {
            System.out.println("Brak parametru, używam domyślnej liczby wątków: 2.");
        }

        data dataPool = new data(1000);
        data sequentialData = new data(dataPool);

        dataPool.printdata();
        ResultsCollector parallelcollector = new ResultsCollector();

        // Uruchomienie wątków
        List<calculations> workers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        long start = System.currentTimeMillis();
        System.out.println("pocz");

        // Równolegle
        for (int i = 0; i < numThreads; i++) {
            calculations worker = new calculations(dataPool, parallelcollector);
            workers.add(worker);
            Thread thread = new Thread(worker, "Worker-" + i);
            threads.add(thread);
            thread.start();
        }



        Thread monitorThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Wpisz 'exit' aby zakończyć aplikację:");
            while (true) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Zamykam aplikację...");
                    for (calculations worker : workers) {
                        worker.stop();
                    }
                    break;
                }
            }
            scanner.close();
        });
        monitorThread.start();

        while (!dataPool.isEmpty()) {}

        for (calculations worker : workers) {
            worker.stop();
        }

        // Oczekiwanie na zakończenie wszystkich wątków
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("koniec");
        long end = System.currentTimeMillis();
        long parallel_processingTime = end - start;



        ResultsCollector sequentialCollector = new ResultsCollector();
        calculations sequentialCalculations = new calculations(sequentialData, sequentialCollector);
        long sequentialStart = System.currentTimeMillis();
        sequentialCalculations.run();
        long sequentialEnd = System.currentTimeMillis();

        long sequential_processingTime = sequentialEnd - sequentialStart;
        /*
        System.out.println("\n\nWyniki Równolegle :\n\ne");
        for (Result res : parallelcollector.getResults()) {
            System.out.println(res);
        }
        System.out.println("\n\nWyniki sekwencyjne :\n\ne");
        for (Result res : sequentialCollector.getResults()) {
            System.out.println(res);
        }
        */
        // wypisanie czasu:
        System.out.println("Czas równolegle: " + parallel_processingTime);
        System.out.println("\nCzas sekwencyjnie: " + sequential_processingTime);
    }
}
