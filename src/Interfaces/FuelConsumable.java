package Interfaces;

import Exceptions.InsufficientFuelException;
import Exceptions.InvalidOperationException;

public interface FuelConsumable {
    void refuel(double amount) throws InvalidOperationException;
    double getFuelLevel();
    double consumeFuel(double distance) throws InsufficientFuelException;
    double getLastFueledAt();
    void setLastFueledAt(double mileage);
}