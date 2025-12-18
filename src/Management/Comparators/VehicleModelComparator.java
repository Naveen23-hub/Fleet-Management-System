package Management.Comparators;

import Vehicles.Vehicle;
import java.util.Comparator;

public class VehicleModelComparator implements Comparator<Vehicle> {
    @Override
    public int compare(Vehicle v1, Vehicle v2) {
        return v1.getModel().compareToIgnoreCase(v2.getModel());
    }
}