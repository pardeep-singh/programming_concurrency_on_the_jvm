package chapter4;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentTotalFileSizeWLatch {
    private ExecutorService service;
    final private AtomicLong pendingFileVisits = new AtomicLong();
    final private AtomicLong totalSize = new AtomicLong();

    // Synchronization point for one or more threads to wait
    // for other threads to reach a point of completion
    final private CountDownLatch latch = new CountDownLatch(1);

    private void updateTotalSizeOfFilesInDir(final File file) {
        long fileSize = 0;
        if(file.isFile())
            fileSize = file.length();
        else {
            final File[] children = file.listFiles();
            if (children != null) {
                for(final File child : children) {
                    if(child.isFile())
                        fileSize += child.length();
                    else {
                        pendingFileVisits.incrementAndGet();
                        service.execute(new Runnable() {
                            public void run() {
                                updateTotalSizeOfFilesInDir(child);
                            }
                        });
                    }
                }
            }
        }
        totalSize.addAndGet(fileSize);
        if (pendingFileVisits.decrementAndGet() == 0)
            latch.countDown();
    }

    private long getTotalSizeOfFile(final String fileName)
            throws Exception {
        service = Executors.newFixedThreadPool(100);
        pendingFileVisits.incrementAndGet();
        try {
            updateTotalSizeOfFilesInDir(new File(fileName));
            latch.await(100, TimeUnit.SECONDS);
            return totalSize.longValue();
        } finally {
            service.shutdown();
        }
    }

    public static void main(String[] args)
            throws Exception {
        final long start = System.nanoTime();

        final long total = new ConcurrentTotalFileSizeWLatch()
                .getTotalSizeOfFile("/Users/pardeep/Documents/work");
        final long end = System.nanoTime();

        System.out.println("Total Size:" + total);
        System.out.println("Time taken:" + (end - start) / 1.0e9);

    }
}
