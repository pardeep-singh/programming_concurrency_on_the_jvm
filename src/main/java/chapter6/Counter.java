package chapter6;

import akka.stm.Atomic;
import akka.stm.Ref;
import static akka.stm.StmUtils.deferred;
import static akka.stm.StmUtils.compensating;

public class Counter {
    private final Ref<Integer> value = new Ref<Integer>(1);

    public void decrement() {
        new Atomic<Integer>(){
            public Integer atomically() {
                deferred(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(
                                "Transaction completed.... send mail etc"
                        );
                    }
                });

                compensating(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(
                                "Transaction aborted... hold the phone");
                    }
                });

                if (value.get() <= 0)
                    throw new RuntimeException("Operation not allowed");

                value.swap(value.get() - 1);
                return value.get();
            }
        }.execute();
    }

    public static void main(String[] args) {
        Counter counter = new Counter();

        counter.decrement();

        try {
            counter.decrement();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
