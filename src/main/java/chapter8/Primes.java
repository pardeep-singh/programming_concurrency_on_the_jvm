package chapter8;

import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.dispatch.Future;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

public class Primes extends UntypedActor {

    public void onReceive(final Object boundsList) {
        final List<Integer> bounds = (List<Integer>) boundsList;
        final int count =
                PrimeFinder.countPrimesInRange(bounds.get(0), bounds.get(1));
        getContext().replySafe(count);
    }

    public static int countPrimes(final int number,
                                  final int numberOfParts) {
        final int chunksPerPartition = number / numberOfParts;
        final List<Future<?>> results = new ArrayList<Future<?>>();
        for (int index = 0; index < numberOfParts; index++) {
            final int lower = index * chunksPerPartition + 1;
            final int upper = (index == numberOfParts - 1) ? number : lower + chunksPerPartition - 1;
            final List<Integer> bounds = Collections.unmodifiableList(
                    Arrays.asList(lower, upper));
            final ActorRef primeFinder = Actors.actorOf(Primes.class).start();
            results.add(primeFinder.sendRequestReplyFuture(bounds));
        }

        int count = 0;
        for(Future<?> result : results)
            count += (Integer)(result.await().result().get());
        Actors.registry().shutdownAll();
        return count;
    }

    public static void main(String[] args) {
        final long start = System.nanoTime();
        final int count = countPrimes(10000000, 1000);
        final long end = System.nanoTime();
        System.out.println("Number of primes is " + count);
        System.out.println("Time taken " + (end - start) / 1.0e9);
    }
}
