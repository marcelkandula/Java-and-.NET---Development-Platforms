package org.example;


import java.util.ArrayList;
import java.util.List;

public class ResultsCollector {
    private final List<Result> results = new ArrayList<>();

    public synchronized void addResult(Result result) {
        results.add(result);
    }

    public synchronized List<Result> getResults() {
        return new ArrayList<>(results);
    }


}
