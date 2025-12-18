package Management;

import Exceptions.InvalidOperationException;
import Interfaces.FuelConsumable;
import Interfaces.Maintainable;
import Vehicles.*;

import Management.Comparators.VehicleModelComparator;
import Management.Comparators.VehicleSpeedComparator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;

public class FleetManager {

    private List<Vehicle> fleet;
    private Set<String> distinctModels;
    private Set<String> vehicleIds;

    //This is the constructor that initializes the collections for the fleet management system
    public FleetManager() {
        this.fleet = new ArrayList<>();
        this.distinctModels = new TreeSet<>();
        this.vehicleIds = new HashSet<>();
    }

    //This is the function that will add a new vehicle to the fleet
    //and will throw an exception if a vehicle with the same ID exists already
    public void addVehicle(Vehicle v) throws InvalidOperationException {
        if (vehicleIds.contains(v.getId())) {
            throw new InvalidOperationException("Vehicle with ID " + v.getId() + " already exists.");
        }
        fleet.add(v);
        vehicleIds.add(v.getId());
        distinctModels.add(v.getModel());
        System.out.println("Vehicle " + v.getId() + " added to the fleet.");
    }

    //This is the function that will remove a vehicle from the fleet
    //and will throw an exception if a vehicle with the ID doesn't exist in the fleet
    public void removeVehicle(String id) throws InvalidOperationException {
        Vehicle vehR = null;
        for (Vehicle v : fleet) {
            if (v.getId().equals(id)) {
                vehR = v;
                break;
            }
        }

        if (vehR != null) {
            fleet.remove(vehR);
            vehicleIds.remove(id);
            rebuildDistinctModels();
            System.out.println("Vehicle " + id + " removed from the fleet.");
        } else {
            throw new InvalidOperationException("Vehicle with ID " + id + " not found.");
        }
    }

    //This is the function that will start journey for all the vehicles for a specific distance
    //and will throw an exception if fuel is not enough in any particular vehicle
    public void startAllJourneys(double distance) {
        System.out.println("\nStarting all journeys for " + distance + " km:");
        for (Vehicle v : fleet) {
            try {
                v.move(distance);
            } catch (Exception exp) {
                System.out.println("\nERROR: Could not start journey for vehicle " + v.getId() + ". Reason: " + exp.getMessage());
            }
        }
    }

    //Helper method that helps to rebuild the set of distinct models
    //this is called after removing a vehicle or loading from a file
    private void rebuildDistinctModels() {
        distinctModels.clear();
        for (Vehicle v : fleet) {
            distinctModels.add(v.getModel());
        }
    }

    //This is a function that will estimate the fuel required for the entire fleet for a given distance
    //and this will not consume any fuel from the vehicles
    public double getTotalFuelConsumption(double distance) {
        double totalFuel = 0.0;
        for (Vehicle v : fleet) {
            if (v instanceof FuelConsumable fc) {
                double efficiency = v.calculateFuelEfficiency();

                if (efficiency > 0) {
                    totalFuel += (distance / efficiency);
                }
            }
        }
        return totalFuel;
    }

    //This function will perform maintenance on the all vehicles
    public void maintainAll() {
        System.out.println("Performing maintenance on all vehicles that need it...");
        for (Vehicle veh : fleet) {
            if (veh instanceof Maintainable) {
                Maintainable mtbVeh = (Maintainable) veh;
                if (mtbVeh.needsMaintenance()) {
                    mtbVeh.performMaintenance();
                }
            }
        }
        System.out.println();
    }

    //This searches the fleet for vehicles of a specific type (like car or truck etc)
    public List<Vehicle> searchByType(Class<?> type) {
        List<Vehicle> vehFound = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (type.isInstance(v)) {
                vehFound.add(v);
            }
        }
        return vehFound;
    }

    //sorts the fleet on the basis of fuel efficiency (high to low)
    public void sortFleetByEfficiency() {
        Collections.sort(fleet);
        System.out.println("\nFleet is sorted by fuel efficiency (highest to lowest).");
    }

    //sorts the fleet on the basis of model name (A-Z)
    public void sortFleetByModel() {
        Collections.sort(fleet, new VehicleModelComparator());
        System.out.println("\nFleet is sorted by model name (A-Z).");
    }

    //sorts the fleet on the basis of speed (high to low)
    public void sortFleetBySpeed() {
        Collections.sort(fleet, new VehicleSpeedComparator());
        System.out.println("\nFleet is sorted by max speed (highest to lowest).");
    }

    //This function will give us a set of unique(distinct) vehicle models
    public Set<String> getDistinctModels() {
        return this.distinctModels;
    }

    //This helps us find the fastest vehicle
    public Vehicle getFastestVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.min(fleet, new VehicleSpeedComparator());
    }

    //This helps us find the slowest vehicle
    public Vehicle getSlowestVehicle() {
        if (fleet.isEmpty()) return null;
        return Collections.max(fleet, new VehicleSpeedComparator());
    }

    //This will give us the summary of our fleet and will give us the complete report
    public String generateReport() {
        StringBuilder rpt = new StringBuilder();
        rpt.append("\n=================================\n");
        rpt.append("********** Fleet Report *********\n\n");
        rpt.append("Total Vehicles: ").append(fleet.size()).append("\n");

        double totalMlg = 0;
        double totalEfficiency = 0;
        int fuelableVehicles = 0;
        int carCount = 0;
        int truckCount = 0;
        int busCount = 0;
        int airplaneCount = 0;
        int cargoShipCount = 0;

        StringBuilder vehicleDetails = new StringBuilder();

        for (Vehicle v : fleet) {

            totalMlg += v.getCurrentMileage();

            if (v instanceof FuelConsumable fc) {
                double efficiency = v.calculateFuelEfficiency();
                if (efficiency > 0) {
                    totalEfficiency += efficiency;
                    fuelableVehicles++;
                }
            }

            if (v instanceof Car) carCount++;
            else if (v instanceof Truck) truckCount++;
            else if (v instanceof Bus) busCount++;
            else if (v instanceof Airplane) airplaneCount++;
            else if (v instanceof CargoShip) cargoShipCount++;

            String maintenanceStatus = "N/A";
            if (v instanceof Maintainable m) {
                maintenanceStatus = String.valueOf(m.needsMaintenance());
            }

            vehicleDetails.append("ID: ").append(v.getId())
                    .append(", Type: ").append(v.getClass().getSimpleName())
                    .append(", Model: ").append(v.getModel())
                    .append(", Top Speed: ").append(v.getMaxSpeed()).append(" km/h")
                    .append(", Mileage: ").append(v.getCurrentMileage()).append(" km")
                    .append(", Status: ").append(maintenanceStatus)
                    .append("\n");
        }

        rpt.append("\n--- Fleet Statistics ---\n");
        rpt.append("Vehicle Counts by Type:\n");
        if (carCount > 0) rpt.append("  - Cars: ").append(carCount).append("\n");
        if (truckCount > 0) rpt.append("  - Trucks: ").append(truckCount).append("\n");
        if (busCount > 0) rpt.append("  - Buses: ").append(busCount).append("\n");
        if (airplaneCount > 0) rpt.append("  - Airplanes: ").append(airplaneCount).append("\n");
        if (cargoShipCount > 0) rpt.append("  - Cargo Ships: ").append(cargoShipCount).append("\n");
        rpt.append("---------------------------\n");

        if (fuelableVehicles > 0) {
            double avgEfficiency = totalEfficiency / fuelableVehicles;
            rpt.append(String.format("\nAverage Fleet Efficiency: %.1f km/L\n", avgEfficiency));
        } else {
            rpt.append("\nAverage Fleet Efficiency: N/A\n");
        }

        rpt.append(String.format("\nTotal Fleet Mileage: %.1f km\n", totalMlg));

        rpt.append("\n---Individual Vehicle Details---\n");
        rpt.append(vehicleDetails.toString());
        rpt.append("----------------------------------\n");

        return rpt.toString();
    }


    //This will give us a list of vehicles that needs a maintenance
    public List<Vehicle> getVehiclesNeedingMaintenance() {
        List<Vehicle> maintenanceList = new ArrayList<>();
        for (Vehicle v : fleet) {
            if (v instanceof Maintainable) {
                Maintainable mtbVeh = (Maintainable) v;
                if (mtbVeh.needsMaintenance()) {
                    maintenanceList.add(v);
                }
            }
        }
        return maintenanceList;
    }

    //saves the entire fleet into a csv file
    public void saveToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Vehicle v : fleet) {
                String type = v.getClass().getSimpleName();
                String line;

                if (v instanceof Car) {
                    Car c = (Car) v;
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%b,%d,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            c.getMileageAtLastService(),
                            c.getFuelLevel(), c.getCurrentPassengers(), c.needsMaintenance(),
                            c.getNumWheels(), c.getLastFueledAt());

                } else if (v instanceof Truck) {
                    Truck t = (Truck) v;
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%.1f,%b,%d,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            t.getMileageAtLastService(),
                            t.getFuelLevel(), t.getCurrentCargo(), t.needsMaintenance(),
                            t.getNumWheels(), t.getLastFueledAt());

                } else if (v instanceof Bus) {
                    Bus b = (Bus) v;
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%.1f,%b,%d,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            b.getMileageAtLastService(),
                            b.getFuelLevel(), b.getCurrentPassengers(), b.getCurrentCargo(), b.needsMaintenance(),
                            b.getNumWheels(), b.getLastFueledAt());

                } else if (v instanceof Airplane) {
                    Airplane a = (Airplane) v;
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f,%.1f,%d,%.1f,%b,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            a.getMileageAtLastService(),
                            a.getFuelLevel(), a.getCurrentPassengers(), a.getCurrentCargo(), a.needsMaintenance(),
                            a.getLastFueledAt());

                } else if (v instanceof CargoShip) {
                    CargoShip cs = (CargoShip) v;
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f,%b,%.1f,%.1f,%b,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            cs.getMileageAtLastService(),
                            cs.getHasSail(), cs.getFuelLevel(), cs.getCurrentCargo(), cs.needsMaintenance(),
                            cs.getLastFueledAt());

                } else {
                    line = String.format("%s,%s,%s,%.1f,%.1f,%.1f",
                            type, v.getId(), v.getModel(), v.getMaxSpeed(), v.getCurrentMileage(),
                            v.getMileageAtLastService());
                }
                writer.println(line);
            }
            System.out.println("Fleet saved successfully to " + filename);
        }
    }

    //loads the fleet data from the csv file and replaces all the data currently in the fleet
    public void loadFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            fleet.clear();
            distinctModels.clear();
            vehicleIds.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                String id = data[1];
                String model = data[2];
                double maxSpeed = Double.parseDouble(data[3]);
                double mileage = Double.parseDouble(data[4]);
                double mileageAtLastService = Double.parseDouble(data[5]);

                Vehicle vehicle = null;
                if (type.equals("Car")) {
                    Car car = new Car(id, model, maxSpeed);
                    car.setFuelLevel(Double.parseDouble(data[6]));
                    car.setCurrentPassengers(Integer.parseInt(data[7]));
                    car.setMaintenanceNeeded(Boolean.parseBoolean(data[8]));
                    car.setMileageAtLastService(mileageAtLastService);
                    double lastFueledAt = Double.parseDouble(data[10]);
                    car.setLastFueledAt(lastFueledAt);
                    vehicle = car;
                }

                else if (type.equals("Truck")) {
                    int wheels = Integer.parseInt(data[9]);
                    Truck truck = new Truck(id, model, maxSpeed, wheels);
                    truck.setFuelLevel(Double.parseDouble(data[6]));
                    truck.setCurrentCargo(Double.parseDouble(data[7]));
                    truck.setMaintenanceNeeded(Boolean.parseBoolean(data[8]));
                    double lastFueledAt = Double.parseDouble(data[10]);
                    truck.setMileageAtLastService(mileageAtLastService);
                    truck.setLastFueledAt(lastFueledAt);
                    vehicle = truck;
                }
                else if (type.equals("Bus")) {
                    int wheels = Integer.parseInt(data[10]);
                    double lastFueledAt = Double.parseDouble(data[11]);
                    Bus bus = new Bus(id, model, maxSpeed, wheels);
                    bus.setFuelLevel(Double.parseDouble(data[6]));
                    bus.setCurrentPassengers(Integer.parseInt(data[7]));
                    bus.setCurrentCargo(Double.parseDouble(data[8]));
                    bus.setMaintenanceNeeded(Boolean.parseBoolean(data[9]));
                    bus.setMileageAtLastService(mileageAtLastService);
                    bus.setLastFueledAt(lastFueledAt);
                    vehicle = bus;
                }
                else if (type.equals("Airplane")) {
                    double lastFueledAt = Double.parseDouble(data[10]);
                    Airplane airplane = new Airplane(id, model, maxSpeed, 35000);
                    airplane.setFuelLevel(Double.parseDouble(data[6]));
                    airplane.setCurrentPassengers(Integer.parseInt(data[7]));
                    airplane.setCurrentCargo(Double.parseDouble(data[8]));
                    airplane.setMaintenanceNeeded(Boolean.parseBoolean(data[9]));
                    airplane.setMileageAtLastService(mileageAtLastService);
                    airplane.setLastFueledAt(lastFueledAt);
                    vehicle = airplane;
                }
                else if (type.equals("CargoShip")) {
                    double lastFueledAt = Double.parseDouble(data[10]);
                    boolean hasSail = Boolean.parseBoolean(data[6]);
                    CargoShip ship = new CargoShip(id, model, maxSpeed, hasSail);
                    ship.setFuelLevel(Double.parseDouble(data[7]));
                    ship.setCurrentCargo(Double.parseDouble(data[8]));
                    ship.setMaintenanceNeeded(Boolean.parseBoolean(data[9]));
                    ship.setMileageAtLastService(mileageAtLastService);
                    ship.setLastFueledAt(lastFueledAt);
                    vehicle = ship;
                }

                if (vehicle != null) {
                    vehicle.setCurrentMileage(mileage);
                    fleet.add(vehicle);
                    distinctModels.add(vehicle.getModel());
                    vehicleIds.add(vehicle.getId());
                }
            }
            System.out.println("Fleet loaded successfully from " + filename);
        }
    }

    //This is a helper function for finding a vehicle by its ID
    //and it will throw the error if the vehicle not found by its ID
    public Vehicle getVehicleById(String id) throws Exception {
        for (Vehicle v : fleet) {
            if (v.getId().equalsIgnoreCase(id)) {
                return v;
            }
        }
        throw new Exception("Vehicle with ID '" + id + "' not found.");
    }
}