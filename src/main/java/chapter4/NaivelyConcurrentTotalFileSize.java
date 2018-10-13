package chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.List;

public class NaivelyConcurrentTotalFileSize {
    private long getTotalSizeOfFilesInDir(final ExecutorService service,
                                          final File file)
            throws InterruptedException, ExecutionException, TimeoutException {
        if (file.isFile()) return file.length();

        long total = 0;

        final File[] children = file.listFiles();

        if (children != null) {
            final List<Future<Long>> partialTotalFutures =
                    new ArrayList<Future<Long>>();
            for (final File child : children) {
                partialTotalFutures.add(service.submit(new Callable<Long>() {
                    public Long call() throws Exception {
                        return getTotalSizeOfFilesInDir(service, child);
                    }
                }));
            }

            for (final Future<Long> partialTotalFuture : partialTotalFutures)
                total += partialTotalFuture.get(100, TimeUnit.SECONDS);
        }

        return total;
    }

    private long getTotalSizeOfFile(final String fileName)
            throws Exception {
        final ExecutorService service = Executors.newFixedThreadPool(100);
        try {
            return getTotalSizeOfFilesInDir(service, new File(fileName));
        } finally {
            service.shutdown();
        }
    }

    public static void main(String[] args)
            throws Exception {
        final long start = System.nanoTime();
        final long total = new NaivelyConcurrentTotalFileSize()
                .getTotalSizeOfFile("/Users/pardeep/Documents/work");
        final long end = System.nanoTime();
        System.out.println("Total Size:" + total);
        System.out.println("Time taken:" + (end - start) / 1.0e9);
    }
}
