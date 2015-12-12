package ru.nntu.mergesort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by gorbatovskiy on 12.12.15.
 */
public class AsyncMergeSort {
    private LinkedList<LinkedList<Integer>> row;
    private ArrayList<Thread> workers;
    private int threadCount;
    private int taskCount;
    private boolean ready;

    public AsyncMergeSort(Integer[] array, int threadCount) {
        this.threadCount = threadCount;
        workers = new ArrayList<>(threadCount);
        row = new LinkedList<>();
        for (int item : array) {
            LinkedList<Integer> temp = new LinkedList<>();
            temp.add(item);
            row.add(temp);
        }
    }

    public synchronized Integer[] sort() {
        for (int i = 0; i < threadCount; i++) {
            workers.add(i, new Thread(new Worker(this)));
        }
        for (int i = 0; i < threadCount; i++) {
            workers.get(i).start();
        }
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < threadCount; i++) {
            workers.get(i).interrupt();
        }
        return row.poll().toArray(new Integer[]{});
    }

    private synchronized Task getTask() {
        if (row.size() > 1) {
            taskCount++;
            return new Task(row.poll(), row.poll());
        } else {
            return null;
        }
    }

    private synchronized void retTask(Task task) {
        LinkedList<Integer> result = task.getResult();
        if (result != null) {
            taskCount--;
            row.add(result);
            if (taskCount == 0 && row.size() == 1) {
                ready = true;
                notifyAll();
            }
        }
    }

    private class Task {
        private LinkedList<Integer> rPart;
        private LinkedList<Integer> lPart;
        private LinkedList<Integer> result;

        public Task(LinkedList<Integer> lPart, LinkedList<Integer> rPart) {
            result = new LinkedList<>();
            this.lPart = lPart;
            this.rPart = rPart;
        }

        public Task execute() {
            while (!lPart.isEmpty() && !rPart.isEmpty()) {
                if (lPart.getFirst() < rPart.getFirst()) {
                    result.add(lPart.poll());
                } else {
                    result.add(rPart.poll());
                }
            }
            while (!lPart.isEmpty()) {
                result.add(lPart.poll());
            }
            while (!rPart.isEmpty()) {
                result.add(rPart.poll());
            }
            return this;
        }

        public LinkedList<Integer> getResult() {
            return result;
        }
    }

    private class Worker implements Runnable {
        private AsyncMergeSort master;
        private Task task;

        public Worker(AsyncMergeSort master) {
            this.master = master;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                task = master.getTask();
                if (task != null) {
                    master.retTask(task.execute());
                }
                Thread.yield();
            }
        }
    }

    public static void main(String[] args) {
        int testSize = 10000000;
        Random random = new Random();
        Integer[] array = new Integer[testSize];
        for (int i = 0; i < testSize; i++) {
            array[i] = random.nextInt(10000);
        }
        long time = System.currentTimeMillis();
        array = new AsyncMergeSort(array, 1).sort();
        System.out.println(System.currentTimeMillis() - time);
//        System.out.println(Arrays.deepToString(array));
    }
}
