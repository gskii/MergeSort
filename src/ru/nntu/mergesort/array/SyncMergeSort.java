package ru.nntu.mergesort.array;

/**
 * Created by gorbatovskiy on 13.12.15.
 */
public class SyncMergeSort {
    private int[] array;
    private int start;
    private int end;

    public SyncMergeSort(int[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    public void sort() {
        sort(start, end);
    }

    public void sort(int left, int right) {
        if (right - left > 1) {
            int middle = (right + left) / 2;
            sort(left, middle);
            sort(middle, right);
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

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Sync merge sort");
        int length = 1_000_000;
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = length - i;
        }

        long time = System.currentTimeMillis();
        new SyncMergeSort(array, 0, array.length).sort();
        System.out.println(System.currentTimeMillis() - time);

        for (int i = 1; i < length; i++) {
            if (array[i] < array[i - 1]) {
                System.out.println("Bad sort at: " + array[i - 1] + " " + array[i]);
            }
        }

    }
}
