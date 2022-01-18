import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class PrimeGenerator implements Runnable {

    // I need a shared Object which with synchrounously assign the next prime number
    // to an available thread
    protected Integer current_prime = 0;
    private final Object lock = new Object();
    protected AtomicBitSet sieve; // By default all bits are set to 0
    // ! Therefore, Non-Primes are True
    protected int SIZE;
    // We can speed up insertions making this a list but keeping it a set will allow
    // me to cut some corners
    protected ConcurrentSkipListSet<Integer> Primes = new ConcurrentSkipListSet<>();
    protected Integer numThreadCompleted = 0;

    public AtomicBitSet getSieve() {
        return sieve;
    }

    public void setSieve(AtomicBitSet sieve) {
        this.sieve = sieve;
    }

    public int getSIZE() {
        return SIZE;
    }

    public void setSIZE(int sIZE) {
        SIZE = sIZE;
    }

    // Constructor
    public PrimeGenerator() {
        this.SIZE = 100000000;
        this.sieve = generateStartingPrimeBitSet(SIZE);
    }

    public PrimeGenerator(int nBits) {
        this.SIZE = nBits;
        this.sieve = generateStartingPrimeBitSet(nBits);
    }

    public ConcurrentSkipListSet<Integer> getPrimesSet() {
        return this.Primes;
    }

    private AtomicBitSet generateStartingPrimeBitSet(int size) {
        AtomicBitSet ret = new AtomicBitSet(size + 1);
        TreeSet<Integer> primeSet = new TreeSet<>(Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19));

        // Manually Set the bitset to contain the first 8 primes so taht all threads can
        // be assigned a starting prime in the worst case.
        for (int i = 0; i < 20; i++) {

            if (!primeSet.contains(i)) {
                // System.out.println("Setting " + i);
                ret.set(i);
            }

        }
        ret.set(SIZE);

        return ret;

    }

    // TODO Implement method
    private synchronized int getNextPrime() {

        synchronized (lock) {

            int ret = current_prime + 1;
            // While the Bitset is true we are reading primes
            while (this.sieve.get(ret)) {
                // Increment
                ret += 1;
                // Check if we are going out of bound of the sieve

                if (Math.floor(Math.sqrt(this.SIZE)) <= ret) {
                    // System.out.println(ret + " too large. Sending -1");
                    return -1;
                }
            }
            // the new prime has been found
            this.current_prime = ret;
            this.Primes.add(this.current_prime);
            // System.out.println("Assigning " + Thread.currentThread().getName() + ": " +
            // this.current_prime);
            return ret;
        }

    }

    @Override
    public void run() {
        int threadNum = Integer.parseInt(Thread.currentThread().getName());
        // Get Assigned a Prime Number to Sieve
        int assignedPrime = -1;
        while (true) {
            assignedPrime = getNextPrime();
            if (assignedPrime == -1) {
                // System.out.println(Thread.currentThread().getName() + ": -1 Detected Moving
                // On to Validation Step ...");
                int threshold = SIZE / 8;
                int start = threshold * threadNum;
                // System.out.println("Done: " + threadNum);
                return;
            }
            // Sieve it
            // System.out.println("Prime: " + assignedPrime + " Thread: " +
            // Thread.currentThread().getName());
            sieve(assignedPrime);
        }

    }

    // private void waitForValidation() {
    // synchronized (this) {
    // this.numThreadCompleted += 1;
    // if (numThreadCompleted == 8) {
    // notifyAll();
    // return;
    // }
    // }
    // wait(1000);
    // System.out.println(numThreadCompleted);

    // try {
    // while (numThreadCompleted != 8) {
    // wait();
    // }

    // } catch (Exception e) {
    // // TODO: handle exception
    // e.printStackTrace();
    // }

    // }

    // Checks the sieve range for primes inclusively
    private void validateSelection(int start, int end) {

        System.out.println("Start: " + start + " End: " + end);
        for (int i = start; i <= end; i++) {
            // Primes are stored as false values
            if (!this.sieve.get(i)) {
                this.Primes.add(i);
            }
        }
    }

    // ! Relies on SIZE variable. Might need to refactor that out if I switch to a
    // segmented sieve
    private void sieve(int assignedPrime) {

        for (int i = 2; assignedPrime * i < SIZE; i++) {
            int multiple = i * assignedPrime;
            this.sieve.set(multiple);
        }
    }

}
