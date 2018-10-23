package chapter8;

public interface EnergySource {
    long getUnitsAvailable();
    long getUsageCount();
    void useEnergy(final long units);
}
