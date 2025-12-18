package simulation;

import Vehicles.Vehicle;
import Interfaces.FuelConsumable;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

public class VehicleTask implements Runnable{

    private Vehicle vehicle;

    //Volatile is used here so that changes to these flags are immediately visible to the thread
    private volatile boolean running=true;
    private volatile boolean paused=false;
    private volatile boolean useSync=false;

    //The label on the GUI that we need to update
    private JLabel statusLabel;

    public VehicleTask(Vehicle vehicle, JLabel statusLabel){
        this.vehicle = vehicle;
        this.statusLabel = statusLabel;
    }

    public void setUseSync(boolean useSync){this.useSync=useSync;}
    public void setPaused(boolean paused){this.paused=paused;}
    public void setRunning(boolean running){this.running=running;}
    public void stop(){this.running=false;}
    public Vehicle getVehicle(){return this.vehicle;}

    @Override
    public void run(){
        while(running){
            try{
                //Simulate time passing (1 second per step)
                Thread.sleep(1000);

                //1. Check Fuel Level First
                //We need to check if the vehicle has enough fuel to actually move the next step.
                //If we don't check this here, the status might briefly show "Paused" before switching to "Out of Fuel".
                boolean isOutOfFuel=false;
                if(vehicle instanceof FuelConsumable){
                    double fuel=((FuelConsumable)vehicle).getFuelLevel();
                    double efficiency=vehicle.calculateFuelEfficiency();

                    //Calculate exactly how much fuel is needed for 1 km
                    double fuelNeeded=(efficiency>0)?(1.0/efficiency):0;

                    //If fuel is basically zero or less than what we need, stop the car
                    if(fuel<=0.0001 || (fuelNeeded>0 && fuel<fuelNeeded)){
                        isOutOfFuel=true;
                        this.paused=true; //Force the pause state
                    }
                }

                //2.Handle Paused State
                if(paused){
                    //This is the fix for the status issue:
                    //If we are paused because the tank is empty, show "Out of Fuel"
                    //If we are paused because the user clicked Pause, show "Paused"
                    if(isOutOfFuel){
                        updateGUI("Out of Fuel");
                    }
                    else{
                        updateGUI("Paused");
                    }
                    continue; // Skip the rest of the loop so we don't add distance
                }

                //3. Move the Vehicle
                try{
                    vehicle.move(1);// Try to move 1 km

                    //This is where the assignment requirements happen:
                    //If sync is on, we use the safe method. If off, we use the buggy one.
                    if(useSync){
                        Highway.addDistanceSafe(1);
                    }
                    else{
                        Highway.addDistanceUnsafe(1);
                    }

                    updateGUI("Running");

                }
                catch(Exception e){
                    //If the move fails (like an exception from the Vehicle class), assume we are out of fuel
                    updateGUI("Out of Fuel");
                    this.paused=true;
                }

            }
            catch(InterruptedException e){
                //If the system stops the thread, we exit the loop
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    //Helper method to update the Swing UI from this background thread
    private void updateGUI(String status){
        String fuelInfo = "";

        //Only show fuel stats if the vehicle actually consumes fuel
        if(vehicle instanceof FuelConsumable){
            double fuel=((FuelConsumable) vehicle).getFuelLevel();
            fuelInfo=String.format(" | Fuel: %.1f L", fuel);
        }

        //Format the final string to display on the screen
        String textToDisplay=String.format("Status: %s | Mileage: %.1f km%s",
                status,
                vehicle.getCurrentMileage(),
                fuelInfo
        );

        //Use invokeLater because Swing components aren't thread-safe
        SwingUtilities.invokeLater(()->{
            statusLabel.setText(textToDisplay);
        });
    }
}