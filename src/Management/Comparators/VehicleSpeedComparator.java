package Management.Comparators;

import Vehicles.Vehicle;
import java.util.Comparator;

public class VehicleSpeedComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return Double.compare(v2.getMaxSpeed(), v1.getMaxSpeed());
    }
}