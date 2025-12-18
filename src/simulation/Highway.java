package simulation;

public class Highway{

    //This is the shared resource (the counter) that all the vehicle threads
    //will try to update at the same time. Because it is static, there is only
    //one highway for all vehicles.
    private static int highwayDistance=0;

    //Resets the counter to zero so we can start a fresh simulation
    public static void reset(){
        highwayDistance=0;
    }

    // Simple getter to let the GUI know the current total distance
    public static int getDistance(){
        return highwayDistance;
    }

    // THIS IS THE BUGGY METHOD
    // We intentionally create a Race Condition here to demonstrate the problem.
    // 1. It reads the current value into 'temp'.
    // 2. It sleeps for a tiny bit (1ms) to force a context switch.
    // 3. While it sleeps, other threads might update 'highwayDistance'.
    // 4. When it wakes up, it overwrites the new value with its old 'temp' calculation,
    //    causing those other updates to be lost forever.
    public static void addDistanceUnsafe(int amount){
        int temp=highwayDistance;
        try{
            Thread.sleep(1);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        highwayDistance=temp+amount;
    }

    // THIS IS THE FIXED METHOD
    // The 'synchronized' keyword acts like a lock. It ensures that only one
    // thread can enter this method at a time. Other threads have to wait their turn,
    // preventing them from overwriting each other's work.
    public static synchronized void addDistanceSafe(int amount){
        highwayDistance+=amount;
    }
}