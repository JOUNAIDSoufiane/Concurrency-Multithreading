package example_threads;

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

        // Create and submit workers.
        Thread[] workers = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            workers[i] = new Thread(new Worker(i));
        }
        for (Thread w : workers) {
            w.start();
        }

        // Sleep for a while and then try to terminate the threads.
        System.out.println("main() starts sleeping");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            // ignore
        }

        System.out.println("main() woke up, terminating threads");
        // Call Thread.interrupt() for each running thread.
        // Threads that don't respond to this may never terminate.
        for (Thread w : workers) {
            w.interrupt();
        }
        for (Thread w : workers) {
            try {
                w.join();
            } catch (InterruptedException e) {
                System.err.println("Unexpected exception");
                e.printStackTrace(System.err);
            }
        }

        System.out.println("Terminated");
    }
}
