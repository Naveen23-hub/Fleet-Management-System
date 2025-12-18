package ui;

import Management.FleetManager;
import Vehicles.*;
import Interfaces.FuelConsumable;
import Interfaces.CargoCarrier;
import Interfaces.Maintainable;
import Interfaces.PassengerCarrier;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        FleetManager fleetManager = new FleetManager();
        Scanner scanner = new Scanner(System.in);

        //Demo is started from here and here we are adding 5 vehicles into the fleet and
        //providing them enough fuel to start the journey for 100km
        System.out.println("Running Initial Demo:\n");
        try {
            Car car = new Car("HR26", "Honda Amaze", 160.0);
            Truck truck = new Truck("UP14", "Tata Truck", 100.0, 10);
            Bus bus = new Bus("DL02", "Volvo Bus", 120.0, 6);
            Airplane airplane = new Airplane("ULT", "Air India Boeing 747", 900.0, 26000);
            CargoShip ship = new CargoShip("B16", "Britannic", 40.0, false);

            fleetManager.addVehicle(car);
            fleetManager.addVehicle(truck);
            fleetManager.addVehicle(bus);
            fleetManager.addVehicle(airplane);
            fleetManager.addVehicle(ship);

            car.refuel(50);
            truck.refuel(200);
            bus.refuel(150);
            airplane.refuel(10000);
            ship.refuel(20000);

            fleetManager.startAllJourneys(100);

            System.out.println("\nInitial Fleet Report after Demo");
            System.out.println(fleetManager.generateReport());

        } catch (Exception e) {
            System.err.println("Error during the demo setup: " + e.getMessage());
        }
        System.out.println("**** Demo is Finished ****\n");
        //Demo ends here


        while (true) {
            //Prints the menu and asks for the function to be performed on the fleet or any vehicle
            printMenu();
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": //This will add a new vehicle to the fleet
                        System.out.print("Enter vehicle type (Car, Truck, Bus, Airplane, CargoShip)(Enter 0 if selected wrong): ");
                        String type = scanner.nextLine();
                        if (type.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            break;
                        }
                        System.out.print("Enter ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Model: ");
                        String model = scanner.nextLine();
                        System.out.print("Enter Max Speed: ");
                        double maxSpeed = Double.parseDouble(scanner.nextLine());

                        if (type.equalsIgnoreCase("Car")) {
                            fleetManager.addVehicle(new Car(id, model, maxSpeed));
                        } else if (type.equalsIgnoreCase("Truck")) {
                            System.out.print("Enter no. of wheels: ");
                            int wheels = Integer.parseInt(scanner.nextLine());
                            fleetManager.addVehicle(new Truck(id, model, maxSpeed, wheels));
                        } else if (type.equalsIgnoreCase("Bus")) {
                            System.out.print("Enter no. of wheels: ");
                            int wheels = Integer.parseInt(scanner.nextLine());
                            fleetManager.addVehicle(new Bus(id, model, maxSpeed, wheels));
                        } else if (type.equalsIgnoreCase("Airplane")) {
                            System.out.print("Enter max altitude: ");
                            double altitude = Double.parseDouble(scanner.nextLine());
                            fleetManager.addVehicle(new Airplane(id, model, maxSpeed, altitude));
                        } else if (type.equalsIgnoreCase("CargoShip")) {
                            System.out.print("Does it have a sail? (true/false): ");
                            boolean hasSail = Boolean.parseBoolean(scanner.nextLine());
                            fleetManager.addVehicle(new CargoShip(id, model, maxSpeed, hasSail));
                        } else {
                            System.out.println("Wrong input, Change vehicle type");
                        }
                        System.out.println();
                        break;

                    case "2"://This will remove the vehicle by ID if present
                        System.out.print("Enter vehicle ID to remove(Enter 0 if selected wrong): ");
                        String removeId = scanner.nextLine();
                        if (removeId.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            break;
                        }
                        fleetManager.removeVehicle(removeId);
                        System.out.println();
                        break;

                    case "3"://This will estimate the fuel consumption for the entire fleet when we enter the distance
                        System.out.print("Enter distance to estimate total fuel (Enter 0 to cancel)(Enter 0 if selected wrong): ");
                        String estDistStr = scanner.nextLine();
                        if (estDistStr.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            break;
                        }
                        double estDist = Double.parseDouble(estDistStr);
                        double totalFuel = fleetManager.getTotalFuelConsumption(estDist);
                        System.out.printf("Total estimated fuel for the entire fleet for %.1f km: %.2f L\n",
                                estDist, totalFuel);
                        System.out.println();
                        break;

                    case "4"://This will refuel all the vehicles in the fleet
                        System.out.print("Amount to refuel(Enter 0 if selected wrong): ");
                        String amountStr = scanner.nextLine();
                        if (amountStr.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            ;
                            break;
                        }
                        double amount = Double.parseDouble(amountStr);
                        List<Vehicle> allVehicles = fleetManager.searchByType(Vehicle.class);
                        for (Vehicle v : allVehicles) {
                            if (v instanceof FuelConsumable) {
                                try {
                                    ((FuelConsumable) v).refuel(amount);
                                } catch (Exception e) {
                                    System.out.println("Could not refuel " + v.getId() + ": " + e.getMessage());
                                    System.out.println();
                                }
                            }
                        }
                        System.out.println("Refueled all applicable vehicles.\n");
                        break;

                    case "5"://This will start the journey for all the vehicles if there is enough fuel in vehicle
                        System.out.print("Distance for the journey(Enter 0 if selected wrong): ");
                        String distanceStr = scanner.nextLine();
                        if (distanceStr.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            ;
                            break;
                        }
                        double distance = Double.parseDouble(distanceStr);
                        fleetManager.startAllJourneys(distance);
                        break;

                    case "6"://This will list all vehicles that need maintenance
                        List<Vehicle> needsMaintenance = fleetManager.getVehiclesNeedingMaintenance();
                        System.out.println("Vehicles needing maintenance: " + needsMaintenance.size());
                        for (Vehicle v : needsMaintenance) {
                            v.displayInfo();
                        }
                        System.out.println();
                        break;

                    case "7"://Performs maintenance on all vehicles
                        fleetManager.maintainAll();
                        break;

                    case "8":{//This will schedule maintenance for a specific vehicle by ID
                        System.out.print("Enter ID of vehicle to schedule maintenance(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) {
                            System.out.println("*Returning to menu*\n");
                            break;
                        }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);
                        if (v instanceof Maintainable) {
                            ((Maintainable) v).scheduleMaintenance();
                            System.out.println("Maintenance scheduled for " + v.getId() + ".");
                            System.out.println();
                        } else {
                            System.out.println("This vehicle is not maintainable.\n");
                        }
                        break;
                    }

                    case "9": {//Boards passengers on the vehicles
                        System.out.print("Enter ID of vehicle to board(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);
                        if (v instanceof PassengerCarrier) {
                            System.out.print("Enter number of passengers to board: ");
                            int count = Integer.parseInt(scanner.nextLine());
                            ((PassengerCarrier) v).boardPassengers(count);
                            System.out.println("Passengers boarded successfully.\n");
                        } else {
                            System.out.println("This vehicle cannot carry passengers.\n");
                        }
                        break;
                    }

                    case "10": {//Disembark passengers from the vehicles
                        System.out.print("Enter ID of vehicle to disembark(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);
                        if (v instanceof PassengerCarrier) {
                            System.out.print("Enter number of passengers to disembark: ");
                            int count = Integer.parseInt(scanner.nextLine());
                            ((PassengerCarrier) v).disembarkPassengers(count);
                            System.out.println("Passengers disembarked successfully.\n");
                        } else {
                            System.out.println("This vehicle cannot carry passengers.\n");
                        }
                        break;
                    }

                    case "11": {//Loads cargo on the vehicles
                        System.out.print("Enter ID of vehicle to load cargo(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);
                        if (v instanceof CargoCarrier) {
                            System.out.print("Enter weight of cargo to load (kg): ");
                            double weight = Double.parseDouble(scanner.nextLine());
                            ((CargoCarrier) v).loadCargo(weight);
                            System.out.println("Cargo loaded successfully.\n");
                        } else {
                            System.out.println("This vehicle cannot carry cargo.\n");
                        }
                        break;
                    }

                    case "12": {//Unloads cargo from the vehicles
                        System.out.print("Enter ID of vehicle to unload cargo(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);
                        if (v instanceof CargoCarrier) {
                            System.out.print("Enter weight of cargo to unload (kg): ");
                            double weight = Double.parseDouble(scanner.nextLine());
                            ((CargoCarrier) v).unloadCargo(weight);
                            System.out.println("Cargo unloaded successfully.\n");
                        } else {
                            System.out.println("This vehicle cannot carry cargo.\n");
                        }
                        break;
                    }

                    case "13": {//This will display a detailed report for a single vehicle by ID
                        System.out.print("Enter ID of vehicle to check(Enter 0 if selected wrong): ");
                        String vehicleId = scanner.nextLine();
                        if (vehicleId.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Vehicle v = fleetManager.getVehicleById(vehicleId);

                        System.out.println("\n--- Status for Vehicle: " + v.getId() + " ---");
                        v.displayInfo();

                        if (v instanceof LandVehicle lv) {
                            System.out.println("Number of Wheels: " + lv.getNumWheels());
                        }

                        if (v instanceof FuelConsumable fc) {
                            System.out.printf("Fuel Level: %.2f L\n", fc.getFuelLevel());
                            System.out.printf("Last Fueled At: %.1f km\n", fc.getLastFueledAt());
                            System.out.printf("Fuel Efficiency: %.1f km/L\n", v.calculateFuelEfficiency());
                        }

                        if (v instanceof PassengerCarrier) {
                            System.out.printf("Passengers: %d / %d\n",
                                    ((PassengerCarrier) v).getCurrentPassengers(),
                                    ((PassengerCarrier) v).getPassengerCapacity());
                        }
                        if (v instanceof CargoCarrier) {
                            System.out.printf("Cargo: %.2f kg / %.2f kg\n",
                                    ((CargoCarrier) v).getCurrentCargo(),
                                    ((CargoCarrier) v).getCargoCapacity());
                        }

                        if (v instanceof Maintainable m) {
                            System.out.println("Needs Maintenance: " + m.needsMaintenance());
                            System.out.printf("Last Maintenance At: %.1f km\n", v.getMileageAtLastService());
                        }
                        System.out.println("---------------------------------\n");
                        break;
                    }

                    case "14"://This will generate the full report for the entire fleet
                        System.out.println(fleetManager.generateReport());
                        break;

                    case "15"://Saves all the necessary information of the vehicles in the csv file
                        fleetManager.saveToFile("my_fleet.csv");
                        break;

                    case "16"://This will load all the data replacing current data from the csv file
                        fleetManager.loadFromFile("my_fleet.csv");
                        break;

                    case "17"://This will search and display vehicles by their type
                        System.out.print("Enter type to search (e.g., Car, Truck, FuelConsumable)(Enter 0 if selected wrong): ");
                        String searchType = scanner.nextLine();
                        if (searchType.equals("0")) { System.out.println("*Returning to menu*\n"); break; }
                        Class<?> neededClass = null;
                        if (searchType.equalsIgnoreCase("Car")) neededClass = Car.class;
                        else if (searchType.equalsIgnoreCase("Truck")) neededClass = Truck.class;
                        else if (searchType.equalsIgnoreCase("Bus")) neededClass = Bus.class;
                        else if (searchType.equalsIgnoreCase("Airplane")) neededClass = Airplane.class;
                        else if (searchType.equalsIgnoreCase("CargoShip")) neededClass = CargoShip.class;
                        else if (searchType.equalsIgnoreCase("FuelConsumable")) neededClass = FuelConsumable.class;

                        if (neededClass != null) {
                            List<Vehicle> results = fleetManager.searchByType(neededClass);
                            System.out.println("Found " + results.size() + " vehicles of type " + searchType + ":");
                            for (Vehicle v : results) {
                                v.displayInfo();
                            }
                            System.out.println();
                        } else {
                            System.out.println("Unrecognized type :(\n");
                        }
                        break;

                    case "18"://This will sort the fleet by fuel efficiency (high to low)
                        fleetManager.sortFleetByEfficiency();
                        System.out.println(fleetManager.generateReport());
                        break;

                    case "19"://Sort the fleet model from A to Z
                        fleetManager.sortFleetByModel();
                        System.out.println(fleetManager.generateReport());

                    case "20"://Sort the fleet by max speed (high to low)
                        fleetManager.sortFleetBySpeed();
                        System.out.println(fleetManager.generateReport());
                        break;

                    case "21": {//This will display the fastest vehicle
                        Vehicle fastest = fleetManager.getFastestVehicle();
                        if (fastest != null) {
                            System.out.println("**** Fastest Vehicle ****");
                            System.out.println("Type: " + fastest.getClass().getSimpleName());
                            fastest.displayInfo();
                            System.out.println();
                        } else {
                            System.out.println("The fleet is empty.");
                            System.out.println();
                        }
                        break;
                    }

                    case "22": {//This will display the slowest vehicle
                        Vehicle slowest = fleetManager.getSlowestVehicle();
                        if (slowest != null) {
                            System.out.println("**** Slowest Vehicle ****");
                            System.out.println("Type: " + slowest.getClass().getSimpleName());
                            slowest.displayInfo();
                            System.out.println();
                        } else {
                            System.out.println("The fleet is empty.");
                            System.out.println();
                        }
                        break;
                    }

                    case "23": {//This will list all the unique vehicle models present in the fleet
                        Set<String> models = fleetManager.getDistinctModels();
                        System.out.println("**** Distinct Vehicle Models (" + models.size() + ") ****");
                        for (String m : models) {
                            System.out.println("- " + m);
                        }
                        System.out.println();
                        break;
                    }

                    case "24"://Terminates the program
                        System.out.println("Exit... BBye!");
                        scanner.close();
                        return;

                    default://Handles the invalid choice
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (Exception e) {//this throws the error when we enter any invalid input in the choice(like string)
                System.out.println("Error occurred: " + e.getMessage());
            }
        }
    }

    //Menu
    private static void printMenu() {
        System.out.println("=================================");
        System.out.println("Fleet Management System Menu:");
        System.out.println();

        System.out.println("Add/Remove operations:");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Remove Vehicle");
        System.out.println();

        System.out.println("Operations on all vehicles:");
        System.out.println("3. Estimate Fleet Fuel for Journey");
        System.out.println("4. Refuel All Vehicles");
        System.out.println("5. Start Journey for All Vehicles");
        System.out.println("6. List Vehicles Needing Maintenance");
        System.out.println("7. Perform Maintenance on All Vehicles");
        System.out.println();

        System.out.println("Operations on a vehicle:");
        System.out.println("8. Schedule Maintenance for a Vehicle");
        System.out.println("9. Board Passengers");
        System.out.println("10. Disembark Passengers");
        System.out.println("11. Load Cargo");
        System.out.println("12. Unload Cargo");
        System.out.println();

        System.out.println("Vehicle Status/Fleet Report:");
        System.out.println("13. Check Vehicle Status");
        System.out.println("14. Generate Fleet Report");
        System.out.println();

        System.out.println("Save/Load:");
        System.out.println("15. Save Fleet to File");
        System.out.println("16. Load Fleet from File");
        System.out.println();

        System.out.println("Search Vehicle:");
        System.out.println("17. Search Vehicles by Type");
        System.out.println();

        System.out.println("Sorting & Reporting:");
        System.out.println("18. Sort Fleet by Efficiency (Default)");
        System.out.println("19. Sort Fleet by Model Name");
        System.out.println("20. Sort Fleet by Max Speed");
        System.out.println("21. Show Fastest Vehicle");
        System.out.println("22. Show Slowest Vehicle");
        System.out.println("23. List Distinct Vehicle Models");
        System.out.println();

        System.out.println("Terminate:");
        System.out.println("24. Exit");
        System.out.println("=================================");
        System.out.println();
    }

}