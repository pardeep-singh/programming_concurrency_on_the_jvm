package chapter5;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RefactoredEnergySource {
    private final long MAXLEVEL = 100;
    private long level = MAXLEVEL;
    private long usage = 0;
    private final ReadWriteLock monitor = new ReentrantReadWriteLock();
    private static final ScheduledExecutorService replenishTimer =
            Executors.newScheduledThreadPool(10);
    private ScheduledFuture<?> replenishTask;

    private RefactoredEnergySource() { }


    private void init() {
        replenishTask = replenishTimer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                replenish();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static RefactoredEnergySource create() {
        final RefactoredEnergySource refactoredEnergySource = new RefactoredEnergySource();
        refactoredEnergySource.init();
        return refactoredEnergySource;
    }

    public  long getUnitsAvailable() {
        monitor.readLock().lock();
        try {
            return this.level;
        } finally {
            monitor.readLock().unlock();
        }
    }

    public long getUsageCount() {
        monitor.readLock().lock();
        try {
            return this.usage;
        } finally {
            monitor.readLock().unlock();
        }
    }

    public boolean useEnergy(final long units) {
        monitor.writeLock().lock();
        try {
            if (units > 0 && this.level >= units) {
                this.level -= units;
                this.usage++;
                return true;
            } else {
                return false;
            }
        } finally {
            monitor.writeLock().unlock();
        }
    }

    // Add synchronized so that changes cross the memory barrier
    public synchronized void stopEnergySource() {
        replenishTask.cancel(false);
    }

    private void replenish() {
        monitor.writeLock().lock();
        try {
            if (this.level < MAXLEVEL) this.level++;
            System.out.println("in Thread:" + this.level);
        } finally {
            monitor.writeLock().unlock();
        }
    }

    public static void main(String[] args)
            throws Exception {
        RefactoredEnergySource refactoredEnergySource = RefactoredEnergySource.create();
        System.out.println("In main thread " + refactoredEnergySource.getUnitsAvailable());
        refactoredEnergySource.useEnergy(5);
        refactoredEnergySource.useEnergy(15);
        refactoredEnergySource.useEnergy(10);
        //Thread.sleep(5000);
        System.out.println("In main thread " + refactoredEnergySource.getUnitsAvailable());
        refactoredEnergySource.stopEnergySource();
        System.out.println("In main thread " + refactoredEnergySource.getUnitsAvailable());
    }

}
