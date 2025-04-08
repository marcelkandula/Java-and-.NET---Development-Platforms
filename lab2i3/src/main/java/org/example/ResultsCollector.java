package org.example;


import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class ResultsCollector implements java.io.Serializable {
    private final List<Result> results = new ArrayList<>();

    public synchronized void addResult(Result result) {
        results.add(result);
    }

    public synchronized List<Result> getResults() {
        return new ArrayList<>(results);
    }


}
