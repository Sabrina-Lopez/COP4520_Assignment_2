import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class problem2 
{

    // A lock for the current visitor viewing the vase
    ReentrantLock guest = new ReentrantLock();

    // True if there is a guest viewing the crystal vase
    AtomicBoolean busyBool = new AtomicBoolean();

    // Total number of guests that can view the vase
    AtomicInteger numGuests = new AtomicInteger();

    // Number of unique guests that viewed the vase
    AtomicInteger visitedGuests = new AtomicInteger();

    // Array of threads for each guest
    guests[] guestThreads;

    // Constructor
    problem2(boolean busyBool, int numGuests, int visitedGuests) 
    {
        this.busyBool = new AtomicBoolean(busyBool);
        this.numGuests = new AtomicInteger(numGuests);
        this.visitedGuests = new AtomicInteger(visitedGuests);
    }

    public static void main(String[] args) 
    {

        long startTime = 0;
        long endTime = 0;
        long time = 0;

        // problem2 vase = new problem2(false, 50, 0);
        // problem2 vase = new problem2(false, 100, 0);
        // problem2 vase = new problem2(false, 150, 0);
        problem2 vase = new problem2(false, 200, 0);

        vase.guestThreads = new guests[vase.numGuests.get()];

        // Create and start each thread
        for (int i = 0; i < vase.numGuests.get(); ++i) 
        {
            // Each guest keeps trying to see the vase
            vase.guestThreads[i] = new guests(i, vase);
        }

        // Start the simulation
        startTime = System.currentTimeMillis();

        for (int i = 0; i < vase.numGuests.get(); ++i) 
        {
            // Start each thread
            vase.guestThreads[i].start();
        }

        for (int i = 0; i < vase.numGuests.get(); ++i) 
        {
            try 
            {
                // Stop the threads
                vase.guestThreads[i].join();
            }

            catch (Exception e) 
            {

                e.printStackTrace();
            }
        }

        // End the simulation and take the difference of the start and end times for the simulation's overall run time
        endTime = System.currentTimeMillis();
        time = endTime - startTime;

        // Print output for the guests that have visited the showroom and the overall time for the simulation
        System.out.println(vase.visitedGuests + " guests viewed the crystal vase.");
        System.out.println("This program took " + time + " ms.");
    }
}

class guests extends Thread 
{
    // Number of the current guest viewing the vase
    AtomicInteger curGuest = new AtomicInteger(0);

    // True if the current guest has seen the vase
    AtomicBoolean seenBool = new AtomicBoolean(false);

    // Number of guests that have already viewed the vase
    AtomicInteger visitedGuests = new AtomicInteger();

    // True if there is a guest viewing the vase
    AtomicBoolean busyBool = new AtomicBoolean();

    // Number of guests viewing the vase
    AtomicInteger numGuests = new AtomicInteger();

    // Lock for the current visitor viewing the vase
    ReentrantLock guest = new ReentrantLock();

    // Chance for if the visitor wants to see the vase again
    int chance = 1;

    problem2 main = new problem2(busyBool.get(), visitedGuests.get(), numGuests.get());

    // Constructor
    guests(int curGuest, problem2 main) 
    {
        this.curGuest = new AtomicInteger(curGuest);
        this.busyBool = main.busyBool;
        this.visitedGuests = main.visitedGuests;
        this.numGuests = main.numGuests;
        this.guest = main.guest;
        this.main = main;
    }

    // All guests try to go to the room to see the vase, and if the room is available, a guest changes the room's availability
    // Guests keep trying to go in until all guests have seen the vase
    public void run() 
    {
        while (main.visitedGuests.get() < numGuests.get()) 
        {
            if (busyBool.get() == false && main.visitedGuests.get() < numGuests.get()) 
            {
                this.viewVaseCheck();
            }
        }
    }

    public void viewVaseCheck() 
    {
        if (main.busyBool.get() == false && main.visitedGuests.get() < numGuests.get() && this.chance == 1) this.viewVase();
    }

    public void viewVase() 
    {
        if (main.visitedGuests.get() < numGuests.get()) 
        {
            // Only one guest can view the vase at a time
            main.guest.lock();

            // Guest stays in the room and views the vase
            main.busyBool.set(true);

            try 
            {
                Thread.sleep(50);

            } catch (InterruptedException e) 
            {
                e.printStackTrace();
            }

            // If this guest hasn't seen the vase before, update the count of unique guests who have seen the vase
            if (this.seenBool.get() == false) 
            {
                this.seenBool.set(true);
                visitedGuests.getAndIncrement();
                main.visitedGuests = visitedGuests;
            }

            // The chance they visit again changes
            Random rand = new Random();
            this.chance = rand.nextInt(2);

            // Guest makes the room available again
            main.busyBool.set(false);

            // Now other guests can view the vase
            main.guest.unlock();
        }
    }
}