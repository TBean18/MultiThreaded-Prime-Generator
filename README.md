# Programming Assignment 1

## Instructions for use

First you would need to compile the Java Code

```bash
cd ./src # Change to the Source Code Directory
javac *.java # Compile all Java files
```

Afterwards, you can now run the application with:

```bash
# Assuming you are still in the same directory where you compiled
java App
```

## Output

The Java program will output the results to a file named `primes.txt` in the same
directory as where you ran the java command.

### Output is Formatted to follow:

```
<execution time>  <total number of primes found>  <sum of all primes found>
<top ten maximum primes, listed in order from lowest to highest>
```

# Design

Firstly, after a quick google search for efficient ways for finding primes up to a given N I found the [Sieve of Eratosthenes](https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes). Which allows for an effective way to find primes up to a large N.

Each thread will get assigned a prime number to sieve, once assigned the thread
will cross off all multiples of that prime on the sieve. Once we have reached
prime P where `P >= sqrt(N)` we can begin the parsing process.

During the parsing process, each thread is assigned an equal section of the sieve
to parse for primes. Checking if a value is prime is as simple as checking a
boolean value represented by the BitSet Sieve. If a value is prime we will add
it to the prime set (Java TreeSet).

While I understand that the assignment requirements could possibly be completed
in a faster runtime if the primes are not stored in an additional prime set, I
feel that defeats the purpose of generating the primes in the first place since
I assume our fictional boss in this scenario would want these primes in some
sort of Iterable format.

## First Iteration

I decided to use Java's BitSet to act as the prime sieve. I figured that I
should initialize the first 20 numbers in the sieve so way each of the 8 threads
could begin work immediately.

I decided to use Java's TreeSet to store the resulting set of parsed primes.

### First Iteration Pitfalls:

#### Pitfall 1 - 'Accidental Single Threading'

```java
for (int i = 0; i < NUM_THREADS; i++) {
    threads[i] = new Thread(r, "" + i);
    threads[i].start();
    threads[i].join();
}
```

Running this block of code would join each thread after creating it. Thus, after
creating the first thread it would wait until the first thread is complete
before starting the next thread.

My solution was to move the `threads[i].join()` to another for loop.

#### Pitfall 2 - Thread-Safe Data Structures:

Once I solved Pitfall 1, my implementation quickly fell apart as I was using
basic java.util data structures when I should have bene leveraging the Java
Concurrent APIs.

### Leveraging Java's Concurrent APIs

The sieve of Eratosthenes would need a sieve data structure (currently modeled
as a BitSet) and a solution set which will store the parsed sieve values (currently modeled with a TreeSet).

Thankfully, Java's concurrent APIs come with a Thread-safe TreeSet which I now use as my solution set.

Although, I quickly realized that Java default concurrent APIs didn't include a
thread-safe BitSet so, I did some research on [how to build a thread safe BitSet](https://stackoverflow.com/questions/12424633/atomicbitset-implementation-for-java).

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

Meanwhile, the compiled output files will be generated in the `bin` folder by default.
