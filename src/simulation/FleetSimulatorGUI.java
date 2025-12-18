package simulation;

import Management.FleetManager;
import Vehicles.*;
import Interfaces.FuelConsumable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FleetSimulatorGUI extends JFrame{

    // We need the manager to handle the collection of vehicles
    private FleetManager fleetManager;

    // Keeping track of tasks and threads so we can control them (pause/stop) later
    private List<simulation.VehicleTask> tasks;
    private List<Thread> threads;
    private boolean simulationRunning = false;

    // UI components that need to be accessed by different methods
    private JLabel lblTotalDistance;
    private JCheckBox chkSync;
    private JPanel pnlVehicles;
    private Timer guiUpdateTimer;

    public FleetSimulatorGUI(){
        setTitle("Fleet Highway Simulator");

        //Try to make the GUI look like a native Windows/Mac app instead of the old Java look
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            e.printStackTrace();
        }

        setSize(800,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //Initialize our lists
        tasks = new ArrayList<>();
        threads = new ArrayList<>();

        //Top Panel for Buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JButton btnStart = new JButton("Start");
        JButton btnPause = new JButton("Pause");
        JButton btnResume = new JButton("Resume");
        JButton btnStop = new JButton("Stop");
        JButton btnRestart = new JButton("Restart");
        chkSync = new JCheckBox("Fix Race Condition");// This toggles the synchronized block in Highway

        //Hook up the buttons to their specific functions
        btnStart.addActionListener(e->startSimulation());
        btnStop.addActionListener(e->stopSimulation());

        //When pausing, we loop through all tasks and tell them to wait
        btnPause.addActionListener(e->{
            for(simulation.VehicleTask task:tasks){
                task.setPaused(true);
            }
        });

        //Resume just flips the paused flag back to false
        btnResume.addActionListener(e->{
            for(simulation.VehicleTask task : tasks){
                task.setPaused(false);
            }
        });

        btnRestart.addActionListener(e->performRestart());

        //If the checkbox is clicked while running, update the sync setting immediately
        chkSync.addActionListener(e->{
            boolean sync = chkSync.isSelected();
            for(simulation.VehicleTask task : tasks) task.setUseSync(sync);
        });

        //Add everything to the top panel
        topPanel.add(btnStart);
        topPanel.add(btnPause);
        topPanel.add(btnResume);
        topPanel.add(btnStop);
        topPanel.add(btnRestart);
        topPanel.add(chkSync);
        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel for Vehicle List ---
        pnlVehicles = new JPanel();
        // Using GridLayout to stack the vehicles vertically with a small gap
        pnlVehicles.setLayout(new GridLayout(0,1,5,5));
        pnlVehicles.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        // ScrollPane is needed in case we add too many vehicles for the window size
        JScrollPane scrollPane = new JScrollPane(pnlVehicles);
        add(scrollPane, BorderLayout.CENTER);

        //Bottom Panel for Stats
        JPanel bottomPanel = new JPanel();
        lblTotalDistance = new JLabel("Total Highway Distance: 0 km");
        lblTotalDistance.setFont(new Font("Arial",Font.BOLD,18));
        bottomPanel.add(lblTotalDistance);
        add(bottomPanel, BorderLayout.SOUTH);

        //Timer to refresh the total distance label every 100ms so it looks real-time
        guiUpdateTimer = new Timer(100, e->{
            lblTotalDistance.setText("Total Highway Distance: "+Highway.getDistance()+" km");
        });

        //Load the initial data
        performRestart();
    }

    //Resets everything to the initial state
    private void performRestart(){
        //Stop whatever is running first
        stopSimulation();
        Highway.reset(); //Reset the static counter

        fleetManager = new FleetManager();
        tasks.clear();
        pnlVehicles.removeAll(); //Clear the UI rows

        try {
            //Adding a few hardcoded vehicles for the assignment demo
            fleetManager.addVehicle(new Car("HR26","Honda City",120));
            fleetManager.addVehicle(new Truck("HR20","Tata Safari",80,10));
            fleetManager.addVehicle(new Bus("HR16","Volvo XC90",100,6));
            fleetManager.addVehicle(new Car("HR56","Maruti 800",140));

            //Give them a tiny bit of fuel to start (20L) so they don't die immediately
            for(Vehicle v:fleetManager.searchByType(Vehicle.class)){
                if(v instanceof FuelConsumable){
                    ((FuelConsumable)v).refuel(20);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        //Create the UI row for each vehicle
        for(Vehicle v:fleetManager.searchByType(Vehicle.class)){
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            row.setPreferredSize(new Dimension(750, 40));

            //Label for ID and Name
            JLabel lblName = new JLabel("  [" + v.getId() + "] " + v.getClass().getSimpleName());
            lblName.setPreferredSize(new Dimension(150, 40));

            //This status label is passed to the thread so it can update it directly
            JLabel lblStatus = new JLabel("Status: Ready | Mileage: 0.0 km");

            //Button to add fuel if the vehicle gets stuck
            JButton btnRefuel = new JButton("Refuel (+10L)");
            btnRefuel.addActionListener(e->{
                if (v instanceof FuelConsumable){
                    try{
                        ((FuelConsumable)v).refuel(10);
                        // If it was paused due to empty tank, unpause it now
                        for(simulation.VehicleTask task:tasks) {
                            if(task.getVehicle() == v){
                                task.setPaused(false);
                            }
                        }
                    }
                    catch(Exception ex){
                        /* ignore errors during refuel */
                    }
                }
            });

            //Disable button if vehicle (like a bicycle/sail) doesn't use fuel
            if(!(v instanceof FuelConsumable)){
                btnRefuel.setEnabled(false);
            }

            //Assemble the row
            row.add(lblName,BorderLayout.WEST);
            row.add(lblStatus,BorderLayout.CENTER);
            row.add(btnRefuel,BorderLayout.EAST);

            pnlVehicles.add(row);

            //Create the logical task for this vehicle
            tasks.add(new simulation.VehicleTask(v,lblStatus));
        }

        //Refresh the panel to show new components
        pnlVehicles.revalidate();
        pnlVehicles.repaint();
        lblTotalDistance.setText("Total Highway Distance: 0 km");
    }

    private void startSimulation(){
        if(simulationRunning){
            return;
        }

        threads.clear();
        simulationRunning=true;
        guiUpdateTimer.start();//Start updating the total distance label

        boolean sync=chkSync.isSelected();

        //Spin up a new thread for every vehicle task
        for(simulation.VehicleTask task:tasks){
            task.setRunning(true);
            task.setUseSync(sync);
            Thread t = new Thread(task);
            threads.add(t);
            t.start();
        }
    }

    private void stopSimulation(){
        simulationRunning = false;

        //Tell the logic tasks to stop their while-loops
        for(simulation.VehicleTask task:tasks){
            task.stop();
        }

        //Interrupt threads to wake them up if they are sleeping
        for (Thread t:threads){
            if (t.isAlive()) {
                t.interrupt();
            }
        }
        guiUpdateTimer.stop();

        //Force one last update to make sure the label matches the internal counter exactly
        lblTotalDistance.setText("Total Highway Distance: "+Highway.getDistance()+" km");
    }

    public static void main(String[] args){
        //Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(()->{
            new FleetSimulatorGUI().setVisible(true);
        });
    }
}