package Vehicles;

import Exceptions.InsufficientFuelException;
import Exceptions.InvalidOperationException;
import Exceptions.OverloadException;
import Interfaces.CargoCarrier;
import Interfaces.FuelConsumable;
import Interfaces.Maintainable;

public class Truck extends LandVehicle implements FuelConsumable, CargoCarrier, Maintainable {
    private double fuelLevel;
    private final double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;
    private double lastFueledAt;

    public Truck(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed, numWheels);
        this.fuelLevel = 0.0;
        this.cargoCapacity = 5000;
        this.currentCargo = 0.0;
        this.maintenanceNeeded = false;
        this.mileageAtLastService = 0.0;
        this.lastFueledAt = 0.0;
    }

    //getter function for lastFueledAt
    @Override
    public double getLastFueledAt() {
        return this.lastFueledAt;
    }

    //setter function for lastFueledAt
    @Override
    public void setLastFueledAt(double mileage) {
        this.lastFueledAt = mileage;
    }

    //getter function for mileage at last service
    public double getMileageAtLastService() {
        return this.mileageAtLastService;
    }

    //setter function for mileage at last service
    public void setMileageAtLastService(double mileage) {
        this.mileageAtLastService = mileage;
    }

    //sets the fuel efficiency
    @Override
    public double calculateFuelEfficiency() {
        if (this.currentCargo > (this.cargoCapacity * 0.5)) {
            return 8.0 * 0.9;
        }
        return 8.0;
    }

    //starts the journey and will throw the error if fuel is not enough
    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cannot be negative.");
        }
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) {
            throw new InsufficientFuelException("Fuel is not enough");
        }
        consumeFuel(distance);
        this.currentMileage += distance;
        System.out.println("Truck is hauling cargo for " + distance + " km...");
    }

    //this will refuel the vehicle and will throw the exception if fuel is negative
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel amount must be positive.");
        }
        this.fuelLevel += amount;
        this.setLastFueledAt(this.currentMileage);
    }

    //getter function for fuel level
    @Override
    public double getFuelLevel() {
        return this.fuelLevel;
    }

    //returns the fuel that is consumed
    @Override
    public double consumeFuel(double distance) throws InsufficientFuelException {
        double fuelConsumed = distance / calculateFuelEfficiency();
        if (fuelConsumed > this.fuelLevel) {
            throw new InsufficientFuelException("Fuel cant be consumed more than available.");
        }
        this.fuelLevel -= fuelConsumed;
        return fuelConsumed;
    }

    //setter function for fuel level
    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    //loads the cargo into the bus and will throw the error if cargo is greater than the capacity
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) {
            throw new OverloadException("Cargo capacity of " + this.cargoCapacity + " kg exceeded.");
        }
        this.currentCargo += weight;
    }

    //unloads the cargo from the bus and will throw the error if cargo is unloaded more than the currently present
    @Override
    public void unloadCargo(double weight) throws InvalidOperationException {
        if (weight > this.currentCargo) {
            throw new InvalidOperationException("Cargo cant be unloaded than the currently loaded.");
        }
        this.currentCargo -= weight;
    }

    //getter function for the upper limit of cargo capacity
    @Override
    public double getCargoCapacity() {
        return this.cargoCapacity;
    }

    //getter function for current cargo
    @Override
    public double getCurrentCargo() {
        return this.currentCargo;
    }

    //setter function for current cargo
    public void setCurrentCargo(double currentCargo) {
        this.currentCargo = currentCargo;
    }

    //marks the vehicle for maintenance
    @Override
    public void scheduleMaintenance() {
        this.maintenanceNeeded = true;
    }

    //returns true if the maintenance needed and false if not
    @Override
    public boolean needsMaintenance() {
        return (this.currentMileage - this.mileageAtLastService) > 10000 || this.maintenanceNeeded;
    }

    //performs the maintenance for the vehicle
    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        this.mileageAtLastService = this.currentMileage;
        System.out.println("Maintenance performed on " + this.getId() + " at " + this.currentMileage + " km.");
    }

    //setter function for maintenance
    public void setMaintenanceNeeded(boolean maintenanceNeeded) {
        this.maintenanceNeeded = maintenanceNeeded;
    }
}