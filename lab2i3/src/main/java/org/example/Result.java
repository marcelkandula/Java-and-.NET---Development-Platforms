package org.example;

import java.io.Serial;
import java.io.Serializable;

public class Result implements Serializable {
    private final int input;
    private final int output;
    public final long processingTime;

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
