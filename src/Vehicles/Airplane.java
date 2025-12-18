package Vehicles;

import Exceptions.InsufficientFuelException;
import Exceptions.InvalidOperationException;
import Exceptions.OverloadException;
import Interfaces.CargoCarrier;
import Interfaces.FuelConsumable;
import Interfaces.Maintainable;
import Interfaces.PassengerCarrier;

public class Airplane extends AirVehicle implements FuelConsumable, PassengerCarrier, CargoCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity;
    private int currentPassengers;
    private final double cargoCapacity;
    private double currentCargo;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;
    private double lastFueledAt;

    //Constructor
    public Airplane(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed, maxAltitude);
        this.fuelLevel = 0.0;
        this.passengerCapacity = 200;
        this.currentPassengers = 0;
        this.cargoCapacity = 10000;
        this.currentCargo = 0.0;
        this.maintenanceNeeded = false;
        this.mileageAtLastService=0.0;
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

    //sets mileage at last service
    public void setMileageAtLastService(double mileage) {
        this.mileageAtLastService = mileage;
    }

    //Fuel efficiency set to 5 (given)
    @Override
    public double calculateFuelEfficiency() {
        return 5.0;
    }

    //starts flying and throws error if fuel not enough
    @Override
    public void move(double distance) throws InvalidOperationException, InsufficientFuelException {
        if (distance < 0) {
            throw new InvalidOperationException("Distance cant be negative.");
        }
        double fuelNeeded = distance / calculateFuelEfficiency();
        if (fuelNeeded > this.fuelLevel) {
            throw new InsufficientFuelException("Fuel is not enough");
        }
        consumeFuel(distance);
        this.currentMileage += distance;
        System.out.println("Airplane: Flying at - " + this.maxAltitude + " ft, For Distance - " + distance + " km.");
    }

    //this will refuel the airplane and will throw an error if amount will be invalid
    @Override
    public void refuel(double amount) throws InvalidOperationException {
        if (amount <= 0) {
            throw new InvalidOperationException("Fuel amount must be positive.");
        }
        this.fuelLevel += amount;
        this.setLastFueledAt(this.currentMileage);
    }

    //getter function for fuelLevel
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

    //setter function for current fuel level
    public void setFuelLevel(double fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    //board the passengers into the plane and will throw error if greater than the capacity
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers + count > this.passengerCapacity) {
            throw new OverloadException("Passenger capacity of " + this.passengerCapacity + " exceeded.");
        }
        this.currentPassengers += count;
    }

    //disembark passengers from the plane and will throw the error if disembarked greater than the passengers
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > this.currentPassengers) {
            throw new InvalidOperationException("More passengers cant be disembark than are on board.");
        }
        this.currentPassengers -= count;
    }

    //getter function for the upper limit of passengers
    @Override
    public int getPassengerCapacity() {
        return this.passengerCapacity;
    }

    //getter function for the current passengers
    @Override
    public int getCurrentPassengers() {
        return this.currentPassengers;
    }

    //setter function for the current passengers
    public void setCurrentPassengers(int currentPassengers) {
        this.currentPassengers = currentPassengers;
    }

    //loads the cargo into the plane and will throw the error if greater than the capacity
    @Override
    public void loadCargo(double weight) throws OverloadException {
        if (this.currentCargo + weight > this.cargoCapacity) {
            throw new OverloadException("Cargo capacity of " + this.cargoCapacity + " kg exceeded.");
        }
        this.currentCargo += weight;
    }

    //unloads the cargo from the plane and will throw the error if unloaded greater than the cargo present in the plane
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

    //getter function for the current cargo
    @Override
    public double getCurrentCargo() {
        return this.currentCargo;
    }

    //setter function for the current cargo
    public void setCurrentCargo(double currentCargo) {
        this.currentCargo = currentCargo;
    }

    //marks the vehicle for maintenance
    @Override
    public void scheduleMaintenance() {
        this.maintenanceNeeded = true;
    }

    //checks the vehicle whether the maintenance is needed
    @Override
    public boolean needsMaintenance() {
        return (this.currentMileage - this.mileageAtLastService) > 10000 || this.maintenanceNeeded;
    }

    //this will perform the maintenance on vehicles
    @Override
    public void performMaintenance() {
        this.maintenanceNeeded = false;
        this.mileageAtLastService = this.currentMileage;
        System.out.println("Maintenance performed on " + this.getId() + " at " + this.currentMileage + " km.");
    }

    //setter function for the maintenance needed
    public void setMaintenanceNeeded(boolean maintenanceNeeded) {
        this.maintenanceNeeded = maintenanceNeeded;
    }
}