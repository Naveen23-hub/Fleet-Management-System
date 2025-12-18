package Vehicles;

import Exceptions.InvalidOperationException;
import Exceptions.InsufficientFuelException;

//this is the abstract class and this is the template for all the vehicles
public abstract class Vehicle implements Comparable<Vehicle> {
    private String id;
    private String model;
    protected double maxSpeed;
    protected double currentMileage;
    protected double mileageAtLastService;

    //constructor
    public Vehicle(String id, String model, double maxSpeed) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle ID cannot be empty.");
        }
        this.id = id;
        this.model = model;
        this.maxSpeed = maxSpeed;
        this.currentMileage = 0.0;
        this.mileageAtLastService = 0.0;
    }

    //abstract methods and these will be overridden in other classes that will be its child
    public abstract void move(double distance) throws InvalidOperationException, InsufficientFuelException;
    public abstract double calculateFuelEfficiency();
    public abstract double estimateJourneyTime(double distance);

    //this will display info
    public void displayInfo() {
        System.out.printf("ID: %s, Model: %s, Max Speed: %.1f km/h, Mileage: %.1f km%n",
                id, model, maxSpeed, currentMileage);
    }

    //these function will also be overridden in other classes otherwise these are getter and setter functions

    public double getCurrentMileage() {
        return currentMileage;
    }

    public void setCurrentMileage(double mileage) {
        this.currentMileage = mileage;
    }

    public String getModel() {
        return this.model;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public String getId() {
        return id;
    }

    public double getMileageAtLastService() {
        return this.mileageAtLastService;
    }

    public void setMileageAtLastService(double mileage) {
        this.mileageAtLastService = mileage;
    }

    //compares to other vehicle
    @Override
    public int compareTo(Vehicle other) {
        return Double.compare(other.calculateFuelEfficiency(), this.calculateFuelEfficiency());
    }
}