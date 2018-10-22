package chapter8;

public class PrimeFinder {
    public static boolean isPrime(final int number) {
        if (number <= 1) return false;
        final int limit = (int) Math.sqrt(number);
        for (int i = 2; i <= limit; i++)
            if(number % i == 0)
                return false;
        return true;
    }

    public static int countPrimesInRange(final int lower, final int upper) {
        int count = 0;
        for (int index = lower; index <= upper; index++)
            if(isPrime(index))
                count += 1;
        return count;
    }
}
