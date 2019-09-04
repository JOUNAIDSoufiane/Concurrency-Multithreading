package example_threads;

public class Worker implements Runnable {

    private boolean interrupted = false;

    private final int threadNo;

    public Worker(int threadNo) {
        this.threadNo = threadNo;
    }

    @Override
    public void run() {
        System.out.println("Worker " + threadNo + " started");
        while (!interrupted) {
            // Loop until interrupt detected.

            // Wait for a while.
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    // interrupt detected by means of an exception.
                    interrupted = true;
                    System.out.println("Worker " + threadNo
                            + " caught InterruptedException");
                    continue;
                }
            }

            // Do some work
            for (int i = 0; i < 100000000; i++) {
                // detect interrupts by explicit call.
                // Note that you only can get an InterruptedException when doing
                // a wait().
                if (Thread.interrupted()) {
                    System.out.println("Worker " + threadNo
                            + ": interrupt detected, i = " + i);
                    interrupted = true;
                    break;
                }
            }
        }
    }
}
