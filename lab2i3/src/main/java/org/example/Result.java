package org.example;

public class Result {
    private final int input;
    private final int output;
    private final long processingTime;

    public Result(int input, int output, long processingTime) {
        this.input = input;
        this.output = output;
        this.processingTime = processingTime;
    }

    @Override
    public String toString() {
        return "Result{input=" + input + ", output=" + output +
                ", time=" + processingTime + "ms}";
    }
}
