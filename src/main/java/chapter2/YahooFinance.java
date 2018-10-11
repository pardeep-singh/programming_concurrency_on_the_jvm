package chapter2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Random;

public class YahooFinance {

    final static String yahooEndPoint = "http://ichart.finance.yahoo.com/table.csv?s=";

    public static double getPrice(final String ticket)
        throws IOException {
        final URL url = new URL( yahooEndPoint + ticket);

        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream())
        );

        // Discard Header
        reader.readLine();

        final String data = reader.readLine();
        final String[] dataItems = data.split(",");
        final double priceIsTheLastValue =
                Double.valueOf(dataItems[dataItems.length - 1]);
        return priceIsTheLastValue;
    }

    public static double getPriceMocked(final String ticker)
            throws InterruptedException{
        Thread.sleep(100);
        return new Random().nextDouble();
    }
    public static void main(String[] args) throws Exception{
        System.out.println(getPriceMocked("AAPL"));
    }
}
