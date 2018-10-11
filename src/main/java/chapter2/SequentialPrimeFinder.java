package chapter2;

public class SequentialPrimeFinder extends AbstractPrimeFinder{

    public int countPrimes(final int number) {
        return countPrimesInRange(1, number);
    }

    public static void main(String[] args) {
        new SequentialPrimeFinder().timeAndCompute(10000000);
    }
}
