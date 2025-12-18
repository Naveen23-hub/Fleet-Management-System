package Vehicles;

//this is the abstract class for water vehicle
public abstract class WaterVehicle extends Vehicle {

    protected boolean hasSail;

    public WaterVehicle(String id, String model, double maxSpeed, boolean hasSail) {
        super(id, model, maxSpeed);
        this.hasSail = hasSail;
    }

    @Override
    public double estimateJourneyTime(double distance) {
        double baseTime = distance / maxSpeed;
        return baseTime * 1.15;
    }

    public boolean getHasSail() {
        return this.hasSail;
    }
}