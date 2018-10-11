package chapter2;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.List;

public class ConcurrentNAV extends AbstractNAV {
    public double computeNetAssetValue(final Map<String, Integer> stocks)
        throws Exception{
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        final double blockingCoefficient = 0.9;
        final int poolsize = (int)(numberOfCores / (1 - blockingCoefficient));

        System.out.println("Number of Cores available is " + numberOfCores);
        System.out.println("Pool Size is " + poolsize);

        final List<Callable<Double>> partitions =
                new ArrayList<Callable<Double>>();

        for(final String ticker : stocks.keySet()) {
            partitions.add(new Callable<Double>() {
                public Double call() throws Exception {
                    return stocks.get(ticker) *
                            YahooFinance.getPriceMocked(ticker);
                }
            });
        }

        final ExecutorService executorPool =
                Executors.newFixedThreadPool(poolsize);

        final List<Future<Double>> valueOfStocks =
                executorPool.invokeAll(partitions, 10000, TimeUnit.SECONDS);

        double netAssetvalue = 0.0;
        for(final Future<Double> valueOfAStock : valueOfStocks) {
            netAssetvalue += valueOfAStock.get();
        }

        executorPool.shutdown();
        return netAssetvalue;
    }

    public static void main(String[] args)
            throws Exception {
        new ConcurrentNAV().timeAndComputeValue();
    }
}
