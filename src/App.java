import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class App {
    // Global Configuration Variables
    private static int NUM_THREADS = 8;

    public static void main(String[] args) throws Exception {
        // Start Timer
        PrimeGenerator primeGen = new PrimeGenerator();
        long startTime = System.currentTimeMillis();

        // Spawn NUM_THREADS Threads
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            String name = "Thread #" + i;
            threads[i] = new Thread(primeGen, "" + i);
            threads[i].start();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();

        }

        // System.out.println("Starting Parse Phase");

        // Parse the primes remaining on the sieve
        Parser parser = new Parser(primeGen);
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new Thread(parser, "" + i);
            threads[i].start();
            // threads[i].join();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();

        }

        // Stop Timer Once the Prime Set has been compiled
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        // Display the result
        ConcurrentSkipListSet<Integer> pSet = primeGen.getPrimesSet();
        printOutputToFile(pSet, duration);

        // System.out.println("Sum total of all primes: " + sum);
        // System.out.println("Size of Primes Set: " + pSet.size());
        // System.out.println("Duration: " + duration);
        // printTopNPrimes(pSet, 10);

    }

    public static void printTopNPrimes(ConcurrentSkipListSet pSet, int N) {
        int ret[] = new int[N];
        Iterator<Integer> iter = pSet.descendingIterator();
        for (int i = 0; i < N; i++) {
            ret[i] = iter.next();
        }
        for (int i = 0; i < N; i++) {
            System.out.println(ret[N - i - 1]);
        }

    }

    public static void printOutputToFile(ConcurrentSkipListSet pSet, long execTime) {
        /*
         * <execution time> <total number of primes found> <sum of all primes found>
         * 
         * <top ten maximum primes, listed in order from lowest to highest>
         */
        int primeSetSize = pSet.size();
        int[] lastTenPrimes = new int[10];
        Iterator<Integer> iter = pSet.descendingIterator();
        long sum = 0;
        for (int i = 0; i < primeSetSize; i++) {
            int val = iter.next();
            sum += val;
            if (i < 10) {
                lastTenPrimes[9 - i] = val;
            }
        }

        try {
            PrintWriter pWriter = new PrintWriter("primes.txt");
            pWriter.printf("%dms %d %d%n", execTime, primeSetSize, sum);
            pWriter.print(Arrays.toString(lastTenPrimes));
            pWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getNUM_THREADS() {
        return NUM_THREADS;
    }

    public static void setNUM_THREADS(int nUM_THREADS) {
        NUM_THREADS = nUM_THREADS;
    }
}
