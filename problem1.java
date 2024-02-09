import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class problem1 extends Thread 
{
    // Lock for the current guest in the labyrinth
    static ReentrantLock guest = new ReentrantLock();

    // Number of guest visting the party
    // static AtomicInteger numGuests = new AtomicInteger(50);
    static AtomicInteger numGuests = new AtomicInteger(100);
    // static AtomicInteger numGuests = new AtomicInteger(150);
    // static AtomicInteger numGuests = new AtomicInteger(200);

    // Number of unique guests that have visited the labyrinth already
    AtomicInteger curGuest = new AtomicInteger(0);

    // True if there is a cupcake at the labyrinth's exit
    static AtomicBoolean cupcakeBool = new AtomicBoolean(true);

    // True if the current guest has eaten the cupcake
    AtomicBoolean eatenBool = new AtomicBoolean(false);

    // Number of guests that have already visited the labyrinth
    static AtomicInteger visitedGuests = new AtomicInteger(1);

    // Constructor
    problem1(int curGuest) 
    {
        this.curGuest = new AtomicInteger(curGuest);
    }

    // The current guest navigates the labyrinth whenever the guest's thread is ran
    // All threads try to go at the same time but only eat if they haven't already
    public void run() 
    {
        // If the total number of guests is greater than how many unique guests visited the labyrinth
        while (visitedGuests.get() < numGuests.get()) 
        {
            // Then a guest can only eat at the labyrinth if they haven't eaten before, representing the unique guests visiting

            // The thread that enters first locks and then sees if they ate already
            guest.lock();

            // Thread 0 is used as the counter of visited guests and replaces the cupcake
            // If there's no cupcake, that means a new guest ate a cupcake and the minotaur's servant brings out another cupcake
            if (this.curGuest.get() == 0 && cupcakeBool.get() == false) 
            {
                visitedGuests.getAndIncrement();
                cupcakeBool.set(true);

            } else if (this.eatenBool.get() == false && cupcakeBool.get() == true && this.curGuest.get() != 0) 
            {
                // If we're not on thread 0, then this thread represents a guest
                // Current guest will eat a cupcake if they haven't already eaten and there is a cupcake available
                this.eatenBool.set(true);
                cupcakeBool.set(false);

            }
            // else, the guest at this thread ate already and is not a unique guest

            // Now other guests can visit the labyrinth
            guest.unlock();
        }
    }

    public static void main(String[] args) 
    {
        // Variables to calculate the total time duration of the simulation 
        long startTime = 0;
        long endTime = 0;
        long time = 0;

        // An array of threads for each guest
        problem1[] guestThreads = new problem1[numGuests.get()];

        // Start the simulation
        startTime = System.currentTimeMillis();

        // Create and start each thread
        for (int i = 0; i < numGuests.get(); ++i) 
        {
            guestThreads[i] = new problem1(i);
            guestThreads[i].start();
        }

        // Keep calling guests to the labyrinth until all the guests have gone inside
        while (visitedGuests.get() < numGuests.get()) 
        {
            // If the amount of unique guests that visited is the total amount of guests
            if (visitedGuests.get() == numGuests.get()) 
            {
                for (int i = 0; i < numGuests.get(); ++i) 
                {
                    try 
                    {
                        // Stop the threads
                        guestThreads[i].join();
                    }
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        // End the simulation and take the difference of the start and end times for the simulation's overall run time
        endTime = System.currentTimeMillis();
        time = endTime - startTime;

        // Print output for the guests that have visited the labyrithn and the overall time for the simulation
        System.out.println(visitedGuests + " guests visited the labyrinth.");
        System.out.println("This program took " + time + " ms.");
    }
}