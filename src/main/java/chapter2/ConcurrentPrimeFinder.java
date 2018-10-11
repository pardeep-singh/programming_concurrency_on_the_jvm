package chapter2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentPrimeFinder extends AbstractPrimeFinder {

    private final int poolSize;
    private final int numberOfParts;

    public ConcurrentPrimeFinder(final int poolSize,
                                 final int numberOfParts) {
        this.poolSize = poolSize;
        this.numberOfParts = numberOfParts;
    }

    public int countPrimes(final int number) {
        int count = 0;

        try {
            final List<Callable<Integer>> partitions =
                    new ArrayList<Callable<Integer>>();

            final int chunksPerPartition = number / numberOfParts;

            for(int i = 0; i < numberOfParts; i++) {
                final int lower = (i * chunksPerPartition) + 1;
                final int upper = (i == numberOfParts + 1) ? number : lower + chunksPerPartition - 1;
                partitions.add(new Callable<Integer>() {
                    public Integer call() throws Exception {
                        return countPrimesInRange(lower, upper);
                    }
                });
            }

            final ExecutorService executorPool = Executors.newFixedThreadPool(poolSize);
            final List<Future<Integer>> resultFromParts =
                    executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);

            executorPool.shutdown();

            for(final Future<Integer> result : resultFromParts) count += result.get();

            return count;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        new ConcurrentPrimeFinder(Runtime.getRuntime().availableProcessors(),
                1000).timeAndCompute(10000000);
    }
}
