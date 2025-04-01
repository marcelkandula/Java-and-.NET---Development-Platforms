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
        dataPool.printdata();
        ResultsCollector collector = new ResultsCollector();

        // Uruchomienie wątków
        List<calculations> workers = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < numThreads; i++) {
            calculations worker = new calculations(dataPool, collector);
            workers.add(worker);
            Thread thread = new Thread(worker, "Worker-" + i);
            threads.add(thread);
            thread.start();
        }

        // Monitorowanie wejścia użytkownika
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wpisz 'exit' aby zakończyć:");
        while (!scanner.nextLine().equalsIgnoreCase("exit")) {
            System.out.println("Wpisz 'exit' aby zakończyć:");
        }

        // Zlecenie zatrzymania wątków
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

        // Wypisanie wyników
        System.out.println("Wyniki :");
        for (Result res : collector.getResults()) {
            System.out.println(res);
        }
    }
}
