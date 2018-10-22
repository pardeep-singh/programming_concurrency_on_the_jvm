package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FileSizeWSTM {
    private ExecutorService service;
    final private Ref<Long> pendingFileVisits = new Ref<Long>(0L);
    final private  Ref<Long> totalSize = new Ref<Long>(0L);
    final private CountDownLatch latch = new CountDownLatch(1);

    private long updatePendingFileVisits(final int value) {
        return new Atomic<Long>() {
            public Long atomically() {
                pendingFileVisits.swap(pendingFileVisits.get() + value);
                return pendingFileVisits.get();
            }
        }.execute();
    }

    private void findTotalSizeOfFilesInDir(final File file) {
        try {
            if(!file.isDirectory()) {
                new Atomic() {
                    public Object atomically() {
                        totalSize.swap(totalSize.get() + file.length());
                        return null;
                    }
                }.execute();
            } else {
                final File[] children = file.listFiles();

                if (children != null) {
                    for(final File child : children) {
                        updatePendingFileVisits(1);
                        service.execute(new Runnable() {
                            @Override
                            public void run() {
                                findTotalSizeOfFilesInDir(child);
                            }
                        });
                    }
                }
            }
            if (updatePendingFileVisits(-1) == 0) latch.countDown();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private long getTotalSizeOfFile(final String fileName)
        throws InterruptedException {
        service = Executors.newFixedThreadPool(100);
        updatePendingFileVisits(1);
        try {
            findTotalSizeOfFilesInDir(new File(fileName));
            latch.await(100, TimeUnit.SECONDS);
            return totalSize.get();
        } finally {
            service.shutdown();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {
        final long start = System.nanoTime();
        final long total = new FileSizeWSTM().getTotalSizeOfFile("/Users/pardeep/Documents/work");
        final long end = System.nanoTime();
        System.out.println("Total Size:" + total);
        System.out.println("Time taken " + (end - start) / 1.0e9);
    }
}
