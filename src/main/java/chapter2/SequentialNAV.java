package chapter2;

import java.util.Map;

public class SequentialNAV extends AbstractNAV{
    public double computeNetAssetValue(final Map<String, Integer> stocks)
            throws Exception {
        double netAssetValue = 0.0;

        for(String ticker : stocks.keySet()) {
            netAssetValue += stocks.get(ticker) *
                    YahooFinance.getPriceMocked(ticker);
        }

        return netAssetValue;
    }

    public static void main(String[] args)
            throws Exception {
        new SequentialNAV().timeAndComputeValue();
    }
}
