package chapter5;

public class EnergySource {
    private final long MAXLEVEL = 100;
    // Access this variable is not protected.
    // Multiple thread can try to access causing
    // inconsistent behaviour.
    private long level = MAXLEVEL;
    private boolean keepRunning = true;

    public EnergySource() {
        // This is violating Class invariant condition
        // which that object should not exposed in an
        // invalid state. Starting thread will cause the
        // memory barrier for this object which is not
        // desirable.
        new Thread(new Runnable() {
            @Override
            public void run() {
                replenish();
            }
        }).start();
    }

    public long getUnitsAvailable() { return level; }

    public boolean useEnergy(final long units) {
        if (units > 0 && level >= units) {
            level -= units;
            return true;
        }
        return false;
    }

    public void stopEnergySource() { keepRunning = false; }

    private void replenish() {
        while(keepRunning) {
            if (this.level < MAXLEVEL) level++;
            System.out.println("In Thread " + this.level);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
        }
    }

    public static void main(String[] args)
            throws Exception {
        EnergySource energySource = new EnergySource();
        System.out.println("In main thread " + energySource.getUnitsAvailable());
        Thread.sleep(5000);
        System.out.println("In main thread " +energySource.getUnitsAvailable());
        energySource.stopEnergySource();
        System.out.println("In main thread " +energySource.getUnitsAvailable());
    }
}
