package ru.nntu.mergesort.array.atomic;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by gorbatovskiy on 13.12.15.
 */
public class AsyncMergeSort implements Runnable {
    private AtomicIntegerArray array;
    private int start;
    private int end;
    private int level;

    public AsyncMergeSort(AtomicIntegerArray array, int start, int end, int level) {
        this.array = array;
        this.level = level;
        this.start = start;
        this.end = end;
    }

    public void sort() throws InterruptedException {
        sort(start, end);
    }

    public void sort(int left, int right) throws InterruptedException {
        if (right - left > 1) {
            int middle = (right + left) / 2;
            if (--level > 0) {
                Thread lTread = new Thread(new AsyncMergeSort(array, left, middle, level));
                Thread rTread = new Thread(new AsyncMergeSort(array, middle, right, level));
                lTread.start();
                rTread.start();
                lTread.join();
                rTread.join();
            } else {
                sort(left, middle);
                sort(middle, right);
            }
            merge(left, middle, right);
        }
    }

    public void merge(int left, int middle, int right) {
        int pos = 0;            // Индекс текущего элемента во временном хранилище
        int ri = middle;        // Индекс текущего элемента правого подмассива
        int li = left;          // Индекс текущего элемента левого подмассива
        int lTail = middle - 1; // Индекс последнего элемента левого подмассива
        int rTail = right - 1;      // Индекс последнего элемента правого подмассива
        int length = right - left;  // Длина временного хранилища
        int[] temp = new int[length];   // Само временное хранилище

        while ((li <= lTail) && (ri <= rTail)) // Перемещение наименьшего элемента
        {                                      // во временное хранилише.
            if (array.get(li) <= array.get(ri))            // Выполняется до тех пор
                temp[pos++] = array.get(li++);       // пока в одном из подмассивов
            else                               // "не останется" элементов
                temp[pos++] = array.get(ri++);
        }

        while (li <= lTail)                    // Перемешение остатков
            temp[pos++] = array.get(li++);           // левого подмассива

        while (ri <= rTail)                    // Перемещение остатков
            temp[pos++] = array.get(ri++);           // правого подмассива

        for (int i = 0; i < length; i++)       // Перемещение данных из
            array.set(left + i, temp[i]);           // временного хранилища
    }

    @Override
    public void run() {
        try {
            sort(start, end);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Async merge sort");
        int length = 1_000_000;
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = length - i;
        }

        AtomicIntegerArray atomicArray = new AtomicIntegerArray(array);

        long time = System.currentTimeMillis();
        new AsyncMergeSort(atomicArray, 0, array.length, 3).sort();
        System.out.println(System.currentTimeMillis() - time);

        for (int i = 1; i < length; i++) {
            if (atomicArray.get(i) < atomicArray.get(i - 1)) {
                System.out.println("Bad sort");
                break;
            }
        }
    }
}
