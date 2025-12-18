package Vehicles;

import Exceptions.InvalidOperationException;

//this is the abstract class for the air vehicle
public abstract class AirVehicle extends Vehicle {

    protected double maxAltitude;

    public AirVehicle(String id, String model, double maxSpeed, double maxAltitude) {
        super(id, model, maxSpeed);
        this.maxAltitude = maxAltitude;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / maxSpeed;
        return baseTime * 0.95;
    }

}