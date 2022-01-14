import java.util.Iterator;
import java.util.TreeSet;

public class App {
    public static void main(String[] args) throws Exception {
        // Start Timer
        PrimeGenerator primeGen = new PrimeGenerator();
        long startTime = System.currentTimeMillis();

        // Spawn 8 Threads
        Thread[] threads = new Thread[8];
        for (int i = 0; i < 8; i++) {
            String name = "Thread #" + i;
            threads[i] = new Thread(primeGen, "" + i);
            threads[i].start();
            threads[i].join();
        }

        System.out.println("Starting Parse Phase");

        // Parse the primes remaining on the sieve
        Parser parser = new Parser(primeGen);
        for (int i = 0; i < 8; i++) {
            threads[i] = new Thread(parser, "" + i);
            threads[i].start();
            threads[i].join();
        }

        // Stop Timer Once the Prime Set has been compiled
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        // Display the result
        TreeSet<Integer> pSet = primeGen.getPrimesSet();
        long sum = 0;
        for (Integer i : pSet) {
            sum += i;
        }

        System.out.println("Sum total of all primes: " + sum);
        Iterator<Integer> iter = primeGen.getPrimesSet().descendingIterator();
        for (int i = 0; i < 10; i++) {

            System.out.println(iter.next());
        }
        System.out.println("Size of Primes Set: " + pSet.size());
        System.out.println("Duration: " + duration);

    }
}
