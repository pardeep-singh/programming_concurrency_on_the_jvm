package chapter2;

public abstract class AbstractPrimeFinder {

    public boolean isPrime(final int number) {
        if (number <= 1) return false;

        for(int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) return false;
        }

        return true;
    }

    public int countPrimesInRange(final int lower, final int upper) {
        int total = 0;

        for(int i = lower; i <= upper; i++) {
            if(isPrime(i)) total++;
        }

        return total;
    }

    public void timeAndCompute(final int number) {
        final long start = System.nanoTime();

        final long numberOfPrimes = countPrimes(number);

        final long end = System.nanoTime();

        System.out.printf("Number of primes under %d is %d\n", number, numberOfPrimes);

        System.out.printf("Time (seconds) taken is " + (end - start) / 1.0e9);
    }

    public abstract int countPrimes(final int number);
}
