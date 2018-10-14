package chapter4;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentTotalFilesSizeWQueue {
    private ExecutorService service;
    final private BlockingQueue<Long> fileSizes =
            new ArrayBlockingQueue<Long>(500);
    final AtomicLong pendingFileVisits = new AtomicLong();

    private void startExploreDir(final File file) {
        pendingFileVisits.incrementAndGet();
        service.execute(new Runnable() {
            public void run() {
                exploreDir(file);
            }
        });
    }

    private void exploreDir(final File file) {
        long fileSize = 0;

        if (file.isFile()) fileSize = file.length();
        else {
            final File[] children = file.listFiles();
            if (children != null)
                for(final File child : children) {
                    if (child.isFile()) fileSize += child.length();
                    else startExploreDir(child);
                }
        }
        try {
            fileSizes.put(fileSize);
        } catch (Exception ex) {
            throw  new RuntimeException(ex);
        }
        pendingFileVisits.decrementAndGet();
    }

    private long getTotalSizeOfFIle(final String fileName)
            throws InterruptedException {
        service = Executors.newFixedThreadPool(100);
        try {
            startExploreDir(new File(fileName));
            long totalSize = 0;
            while (pendingFileVisits.get() > 0 || fileSizes.size() > 0) {
                final Long size = fileSizes.poll(10, TimeUnit.SECONDS);
                totalSize += size;
            }
            return totalSize;
        } finally {
            service.shutdown();
        }
    }

    public static void main(String[] args)
            throws InterruptedException {
        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFilesSizeWQueue()
                .getTotalSizeOfFIle("/Users/pardeep/Documents/work");
        final long end = System.nanoTime();
        System.out.println("Total Size:" + total);
        System.out.println("Time telne:" + (end - start) / 1.0e9);

    }
}
