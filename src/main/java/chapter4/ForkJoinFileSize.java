package chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.List;

public class ForkJoinFileSize {
    private final static ForkJoinPool forkJoinPool = new ForkJoinPool();

    private static class FileSizeFinder extends RecursiveTask<Long> {
        final File file;

        public FileSizeFinder(final File file) {
            this.file = file;
        }

        @Override public Long compute() {
            long size = 0;
            if (file.isFile()) size = file.length();
            else {
                final File[] children = file.listFiles();
                if (children != null) {
                    List<ForkJoinTask<Long>> tasks =
                            new ArrayList<ForkJoinTask<Long>>();
                    for(final File child : children) {
                        if (child.isFile()) size += child.length();
                        else tasks.add(new FileSizeFinder(child));
                    }

                    for (final ForkJoinTask<Long> task : invokeAll(tasks))
                        size += task.join();
                }
            }
            return size;
        }
    }

    public static void main(String[] args) {
        final long start = System.nanoTime();
        final long total = forkJoinPool.invoke(
                new FileSizeFinder((new File("/Users/pardeep/Documents/work"))));
        final long end = System.nanoTime();
        System.out.println("Total Size:" + total);
        System.out.println("Time telne:" + (end - start) / 1.0e9);
    }
}
