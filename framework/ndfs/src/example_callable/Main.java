package example_callable;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    // Creates a thread pool, starts a number of tasks, sleeps for a while,
    // and then tries to terminate the threads.
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(
                    "One argument expexted, the number of threads to use.");
            System.exit(1);
        }
        int nThreads = 1;
        try {
            nThreads = new Integer(args[0]);
        } catch (Throwable e) {
            System.err.println("Expected a number");
            System.exit(1);
        }

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        CompletionService<Void> ecs = new ExecutorCompletionService<Void>(pool);

        // Create and submit workers.
        Worker[] workers = new Worker[nThreads];
        for (int i = 0; i < nThreads; i++) {
            workers[i] = new Worker(i);
        }
        for (Worker w : workers) {
            ecs.submit(w);
        }

        // Sleep for a while and then try to terminate the threads.
        System.out.println("main() starts sleeping");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            // ignore
        }

        System.out.println("main() woke up, terminating threadpool");
        // Note: shutdownNow will not terminate the threads. Rather, it will
        // call Thread.interrupt() for each running thread.
        // Threads that don't respond to this may never terminate.
        pool.shutdownNow();
        try {
            // Wait for the pool to actually terminate.
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        System.out.println("Terminated");
    }
}
