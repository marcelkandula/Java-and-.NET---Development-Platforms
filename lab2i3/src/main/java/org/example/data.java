package org.example;
import java.util.LinkedList;
import java.util.Queue;

public class data {
    private Queue<Integer> data = new LinkedList<>();

    public data(int count){
        for (int i = 0; i < count; i++){
            data.add((int)(Math.random()*1000));
        }
    }

    public synchronized Integer getNext(){
        return data.poll();
    }

    public void printdata(){
        for (Integer elem : data){
            System.out.println(elem);
        }
    }

    public synchronized boolean isEmpty() {
        return data.isEmpty();
    }
}
