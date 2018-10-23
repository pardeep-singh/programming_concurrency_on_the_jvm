package chapter8;

import akka.actor.TypedActor;

public class EnergySourceImpl extends TypedActor implements EnergySource {
    private final long MAXLEVEL = 100L;
    private long level = MAXLEVEL;
    private long usageCount = 0L;

    public long getUnitsAvailable() { return level; }

    public long getUsageCount() { return usageCount; }

    public void useEnergy(final long units) {
        if (units > 0 && level - units >= 0) {
            System.out.println(
                    "Thread in useEnergy: " + Thread.currentThread().getName()
            );
            level -= units;
            usageCount++;
        }
    }

    public static void main(String[] args)
            throws InterruptedException{
        System.out.println("Thread in main: " + Thread.currentThread().getName());
        final EnergySource energySource = TypedActor.newInstance(EnergySource.class, EnergySourceImpl.class);

        System.out.println("Energy Units " + energySource.getUnitsAvailable());

        System.out.println("Firing two requests for use energy");

        energySource.useEnergy(10);
        energySource.useEnergy(10);

        System.out.println("Fired two requests for use energy");

        Thread.sleep(100);

        System.out.println("Firing one more request for use energy");
        energySource.useEnergy(10);

        Thread.sleep(1000);

        System.out.println("Energy Units " + energySource.getUnitsAvailable());
        System.out.println("Usage " + energySource.getUsageCount());

        TypedActor.stop(energySource);

    }
}
