package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;
import akka.stm.TransactionalMap;

import static scala.collection.JavaConversions.asJavaIterable;

import java.util.Iterator;

public class Scores {
    final private TransactionalMap<String, Integer> scoreValues =
            new TransactionalMap<String, Integer>();
    final private Ref<Long> updates = new Ref<Long>(0L);

    public void updateScores(final String name, final int score) {
        new Atomic() {
            public Object atomically() {
                scoreValues.put(name, score);
                updates.swap(updates.get() + 1);
                if (score == 13)
                    throw new RuntimeException("Reject this score");
                return null;
            }
        }.execute();
    }

    public Iterable<String> getNames() {
        return asJavaIterable(scoreValues.keySet());
    }

    public long getNumberOfUpdates() {
        return updates.get();
    }

    public int getScore(final String name) {
        return scoreValues.get(name).get();
    }

    public static void main(String[] args) {
        final Scores scores = new Scores();
        scores.updateScores("Joe", 14);
        scores.updateScores("Sally", 15);
        scores.updateScores("Bernie", 12);

        System.out.println("Number of updates " + scores.getNumberOfUpdates());

        try {
            scores.updateScores("Bill", 13);
        } catch (Exception ex) {
            System.out.println("Update failed for score 13");
        }

        System.out.println("Number of updates " + scores.getNumberOfUpdates());

        for (String name : scores.getNames()) {
            System.out.println(
                    String.format("Score for %s is %d",
                            name,
                            scores.getScore(name)));
        }
    }
}
