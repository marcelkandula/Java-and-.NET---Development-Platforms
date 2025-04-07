package org.example;

public class calculations implements Runnable {
    private final data dataPool;
    private final ResultsCollector collector;
    private volatile boolean running = true;

    public calculations(data dataPool, ResultsCollector collector) {
        this.dataPool = dataPool;
        this.collector = collector;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            Integer data = dataPool.getNext();
            if (data == null) {
                break;
            }
            long start = System.currentTimeMillis();
            int resultValue = computation(data);
            long end = System.currentTimeMillis();
            long processingTime = end - start;
            Result result = new Result(data, resultValue, processingTime);
            collector.addResult(result);
        }
    }
    private int computation(int input){
        int result = 1;

        int iterations = 1400 * input + 13;
        for (int i = 1; i<= iterations; i++){
            result = (result * i + input) % 14032040;
            result = result - (int)(result/i - Math.sqrt(result));
        }
        return result;
    }
}
