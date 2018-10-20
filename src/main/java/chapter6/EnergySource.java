package chapter6;


import akka.stm.Atomic;
import akka.stm.Ref;
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class EnergySource {
    private final long MAXLEVEL = 100;
    final Ref<Long> level = new Ref<Long>(MAXLEVEL);
    final Ref<Long> usageCount = new Ref<Long>(0L);
    final Ref<Boolean> keepRunning = new Ref<Boolean>(true);
    private final static ScheduledExecutorService replenishTimer =
            Executors.newScheduledThreadPool(10);

    private EnergySource() {}

    private void init() {
        System.out.println(level.get());
        replenishTimer.schedule(new Runnable() {
            @Override
            public void run() {
                replenish();
                if (keepRunning.get())
                    replenishTimer.schedule(
                            this,
                            1,
                            TimeUnit.SECONDS
                    );
            }
        }, 1, TimeUnit.SECONDS);
    }

    public static EnergySource create() {
        final EnergySource energySource = new EnergySource();
        energySource.init();
        return energySource;
    }

    public void stopEnergySource() { keepRunning.swap(false); }

    public long getUnitsAvailable() { return level.get(); }

    public long getUsageCount() { return usageCount.get(); }

    public boolean useEnergy(final long units) {
        return new Atomic<Boolean>() {
            public Boolean atomically() {
                long currentLevel = level.get();
                if (units > 0 && currentLevel >= units) {
                    level.swap(currentLevel - units);
                    usageCount.swap(usageCount.get() + 1);
                    return  true;
                } else {
                    return false;
                }
            }
        }.execute();
    }

    private void replenish() {
        new Atomic() {
            public Object atomically() {
                long currentLevel = level.get();
                if (currentLevel < MAXLEVEL) level.swap(currentLevel + 1);
                return null;
            }
        }.execute();
    }

    public static void main(String[] args)
            throws InterruptedException, ExecutionException {

        final EnergySource energySource = EnergySource.create();

        System.out.println("Energy level at start: " + energySource.getUnitsAvailable());

        List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();

        for(int i = 0; i < 10; i++) {
            tasks.add(new Callable<Object>() {
                public Object call() {
                    for(int i = 0; i < 7; i++)
                        energySource.useEnergy(1);
                    return null;
                }
            });
        }

        final ExecutorService service = Executors.newFixedThreadPool(10);
        service.invokeAll(tasks);

        System.out.println("Energy level at end:" + energySource.getUnitsAvailable());

        System.out.println("Usage:" + energySource.getUsageCount());

        service.shutdown();
    }
}
