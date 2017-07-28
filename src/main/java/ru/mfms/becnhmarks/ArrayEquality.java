package ru.mfms.becnhmarks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayEquality {


    public int workers = Runtime.getRuntime().availableProcessors() + 1;
    private final ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() + 1);

    public boolean arrayEquals(byte[] first, byte[] second) throws InterruptedException {
        AtomicBoolean equals = new AtomicBoolean(true);
        if (first == second) {
            return true;
        }
        if (first.length != second.length) {
            return false;
        }
        int size = first.length / workers;
        List<Runnable> tasks = new ArrayList<>(workers);
        int start = 0;
        for (int i = 0; i < workers; i++) {
            int end = start + size;
            tasks.add(getTask(first, second, equals, start, end));

            start = end+1;
        }
        long startTime = System.nanoTime();
        tasks.forEach(pool::submit);

        pool.shutdown();
        pool
                .awaitTermination(1, TimeUnit.MINUTES);
        long took  = System.nanoTime() -startTime;
        return equals.get();

    }

    protected Runnable getTask(byte[] first, byte[] second, AtomicBoolean equals, int start, int end) {
        return new Task(first, second, start, end > first.length ? first.length : end, equals);
    }

    protected byte[] cloneArray(byte[] first, int start, int realyEnd) {

        byte [] bytes = new byte[realyEnd-start];
        System.arraycopy(first,start,bytes,0,realyEnd-start);
        return bytes;
    }

    private static class Task implements Runnable {
        private final byte[] first;
        private final byte[] second;
        private final int from;
        private final int to;
        private final AtomicBoolean equals;

        public Task(byte[] first, byte[] second, int from, int to, AtomicBoolean equals) {
            this.first = first;
            this.second = second;
            this.from = from;
            this.to = to;
            this.equals = equals;
        }

        @Override
        public void run() {
            for (int i = from; i < to && equals.get(); i++) {
                if (first[i] != second[i]) {
                    equals.set(false);
                }
            }
        }
    }

}
