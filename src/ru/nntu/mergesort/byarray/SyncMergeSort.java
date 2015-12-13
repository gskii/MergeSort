package ru.nntu.mergesort.byarray;

/**
 * Created by gorbatovskiy on 13.12.15.
 */
public class SyncMergeSort implements Runnable {
    private int[] array;
    private int start;
    private int end;

    public SyncMergeSort(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    public synchronized void sort(int left, int right) {
        if (right - left > 1) {
            int middle = (right + left) / 2;
            System.out.println("sort1");
            sort(left, middle);
            System.out.println("sort2");
            sort(middle, right);
            System.out.println("merge");
            merge(left, middle, right);
        }
    }

    public synchronized void merge(int left, int middle, int right) {
        System.out.println("merge");
        int pos = 0;            // Индекс текущего элемента во временном хранилище
        int ri = middle;        // Индекс текущего элемента правого подмассива
        int li = left;          // Индекс текущего элемента левого подмассива
        int lTail = middle - 1; // Индекс последнего элемента левого подмассива
        int rTail = right - 1;      // Индекс последнего элемента правого подмассива
        int length = right - left;  // Длина временного хранилища
        int[] temp = new int[length];   // Само временное хранилище

        while ((li <= lTail) && (ri <= rTail)) // Перемещение наименьшего элемента
        {                                      // во временное хранилише.
            if (array[li] <= array[ri])            // Выполняется до тех пор
                temp[pos++] = array[li++];       // пока в одном из подмассивов
            else                               // "не останется" элементов
                temp[pos++] = array[ri++];
        }

        while (li <= lTail)                    // Перемешение остатков
            temp[pos++] = array[li++];           // левого подмассива

        while (ri <= rTail)                    // Перемещение остатков
            temp[pos++] = array[ri++];           // правого подмассива

        for (int i = 0; i < length; i++)       // Перемещение данных из
            array[left + i] = temp[i];           // временного хранилища
    }

    @Override
    public void run() {
        sort(start, end);
    }

    public static void main(String[] args) throws InterruptedException {
        long time = System.currentTimeMillis();
        int[] array = new int[]{9, 8, 7, 6, 5, 4, 3, 2, 1, 0};

        Thread lThread = new Thread(new SyncMergeSort(array, 0, array.length / 2));
        Thread rThread = new Thread(new SyncMergeSort(array, array.length / 2, array.length));

        lThread.start();
        rThread.start();

        lThread.join();
        rThread.join();

        new SyncMergeSort(array, 0, array.length).merge(0, array.length / 2, array.length);

        System.out.println(System.currentTimeMillis() - time);
        for (int item : array) {
            System.out.print(item + " ");
        }
//        executor.shutdown();

    }
}
