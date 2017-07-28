package ru.mfms.becnhmarks;

import java.util.concurrent.atomic.AtomicBoolean;

public class ArrayEqualityCopyArray extends ArrayEquality {
    @Override
    protected Runnable getTask(byte[] first, byte[] second, AtomicBoolean equals, int start, int end) {
        int realyEnd = end > first.length ? first.length : end;
        return new PreparedChunk(cloneArray(first, start, realyEnd), cloneArray(second, start, realyEnd), equals);
    }

    private static class PreparedChunk implements Runnable {
        private final byte[] first;
        private final byte[] second;
        private final AtomicBoolean equals;

        public PreparedChunk(byte[] first, byte[] second, AtomicBoolean equals) {
            this.first = first;
            this.second = second;
            this.equals = equals;
        }

        @Override
        public void run() {
            for (int i = 0; i < first.length && equals.get(); i++) {
                if (first[i] != second[i]) {
                    equals.set(false);
                }
            }
        }
    }
}
