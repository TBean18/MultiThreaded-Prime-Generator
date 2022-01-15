import java.util.BitSet;

public class Parser implements Runnable {

    private AtomicBitSet sieve;
    protected PrimeGenerator pg;

    public Parser(PrimeGenerator pg) {
        this.sieve = pg.sieve;
        this.pg = pg;
    }

    @Override
    public void run() {
        int threadNum = Integer.parseInt(Thread.currentThread().getName());
        int threshold = this.pg.SIZE / 8;
        validateSelection(threshold * threadNum, threshold * (threadNum + 1));

    }

    // Checks the sieve range for primes inclusively
    private void validateSelection(int start, int end) {

        System.out.println("Start: " + start + " End: " + end);
        for (int i = start; i <= end; i++) {
            // Primes are stored as false values
            if (!this.sieve.get(i)) {
                this.pg.Primes.add(i);
            }
        }
    }
}
