package Vehicles;

//this is the abstract class for the land vehicle
public abstract class LandVehicle extends Vehicle {
    private int numWheels;

    public LandVehicle(String id, String model, double maxSpeed, int numWheels) {
        super(id, model, maxSpeed);
        this.numWheels = numWheels;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / maxSpeed;
        return baseTime * 1.1;
    }

    public int getNumWheels() {
        return this.numWheels;
    }

}