package Vehicles;

import Exceptions.InsufficientFuelException;
import Exceptions.InvalidOperationException;
import Exceptions.OverloadException;
import Interfaces.FuelConsumable;
import Interfaces.Maintainable;
import Interfaces.PassengerCarrier;

public class Car extends LandVehicle implements FuelConsumable, PassengerCarrier, Maintainable {

    private double fuelLevel;
    private final int passengerCapacity;
    private int currentPassengers;
    private boolean maintenanceNeeded;
    private double mileageAtLastService;
    private double lastFueledAt;

    public Car(String id, String model, double maxSpeed) {
        super(id, model, maxSpeed, 4);
        this.fuelLevel = 0.0;
        this.passengerCapacity = 5; // As per PDF
        this.currentPassengers = 0;
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
        System.out.println("Car is driving on the road for " + distance + " km...");
    }

    public void setCurrentMileage(double mileage) {
        this.currentMileage = mileage;
    }

    //sets the fuel efficiency
    @Override
    public double calculateFuelEfficiency() {
        return 15.0;
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

    //setter function for fuel level
    public void setFuelLevel(double fuelLevel){
        this.fuelLevel = fuelLevel;
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

    //board the passengers into the bus and will throw the error if passengers are greater than the capacity
    @Override
    public void boardPassengers(int count) throws OverloadException {
        if (this.currentPassengers + count > this.passengerCapacity) {
            throw new OverloadException("Passenger capacity exceeded.");
        }
        this.currentPassengers += count;
    }

    //disembark the passengers from the bus and will throw the error if passengers are removed greater than the already
    //present
    @Override
    public void disembarkPassengers(int count) throws InvalidOperationException {
        if (count > this.currentPassengers) {
            throw new InvalidOperationException("Cannot disembark more passengers than are on board.");
        }
        this.currentPassengers -= count;
    }

    //getter function for the upper limit of passenger capacity
    @Override
    public int getPassengerCapacity() {
        return this.passengerCapacity;
    }

    //getter function for current passengers
    @Override
    public int getCurrentPassengers() {
        return this.currentPassengers;
    }

    //setter function for current passengers
    public void setCurrentPassengers(int passengers) {
        this.currentPassengers = passengers;
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