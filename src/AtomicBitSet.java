import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicBitSet {
    private final AtomicIntegerArray arr;

    public AtomicBitSet(int size) {
        int intLength = (size + 31) >>> 5;
        arr = new AtomicIntegerArray(intLength);
    }

    public void set(long n) {
        int bit = 1 << n;
        int index = (int) (n >>> 5);

        while (true) {
            int num = arr.get(index);
            int num2 = num | bit;
            // If the swap has already occured in another thread
            // OR the swap occurs now successfully, return
            if (num == num2 || arr.compareAndSet(index, num, num2))
                return;
        }
    }

    public boolean get(long n) {
        int bit = 1 << n;
        int index = (int) (n >>> 5);

        int num = arr.get(index);
        return (num & bit) != 0;

    }

}
