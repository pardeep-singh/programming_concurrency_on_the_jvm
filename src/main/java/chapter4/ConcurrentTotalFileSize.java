package chapter4;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentTotalFileSize {
    class SubDirectoriesAndSize {
        final public long size;
        final public List<File> subDirectories;

        public SubDirectoriesAndSize(final long size,
                                     final List<File> subDirectories) {
            this.size = size;
            this.subDirectories = Collections.unmodifiableList(subDirectories);
        }
    }

    private SubDirectoriesAndSize getTotalAndSubDirs(final File file) {
        long total = 0;
        final List<File> subDirectories = new ArrayList<File>();

        if (file.isDirectory()) {
            final File[] children = file.listFiles();

            if (children != null)
                for (final File child : children) {
                    if (child.isFile())
                        total += child.length();
                    else
                        subDirectories.add(child);

                }
        }

        return new SubDirectoriesAndSize(total, subDirectories);
    }

    private long getTotalSizeOfFilesInDir(final File file)
        throws Exception {
        final ExecutorService service = Executors.newFixedThreadPool(100);
        try {
            long total = 0;
            final List<File> directories = new ArrayList<File>();
            directories.add(file);
            while (!directories.isEmpty()) {
                final List<Future<SubDirectoriesAndSize>> partialResults =
                        new ArrayList<Future<SubDirectoriesAndSize>>();

                for(final File directory : directories) {
                    partialResults.add(
                            service.submit(new Callable<SubDirectoriesAndSize>() {
                                public SubDirectoriesAndSize call() throws Exception {
                                    return getTotalAndSubDirs(directory);
                                }
                            })
                    );
                }
                directories.clear();

                for (final Future<SubDirectoriesAndSize> partialResultFuture : partialResults) {
                    final SubDirectoriesAndSize subDirectoriesAndSize =
                            partialResultFuture.get(100, TimeUnit.SECONDS);
                    directories.addAll(subDirectoriesAndSize.subDirectories);
                    total += subDirectoriesAndSize.size;
                }
            }
            return total;
        } finally {
            service.shutdown();
        }
    }

    public static void main(String[] args)
            throws Exception {
        final long start = System.nanoTime();
        final long total = new ConcurrentTotalFileSize()
                .getTotalSizeOfFilesInDir(new File("/Users/pardeep/Documents/work"));
        final long end = System.nanoTime();
        System.out.println("Total Size:" + total);
        System.out.println("Time taken:" + (end - start) / 1.0e9);
    }
}
