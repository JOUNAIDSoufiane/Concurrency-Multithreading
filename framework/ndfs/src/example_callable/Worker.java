package example_callable;

import java.util.concurrent.Callable;

public class Worker implements Callable<Void> {

    private boolean interrupted = false;

    private final int taskNo;

    public Worker(int taskNo) {
        this.taskNo = taskNo;
    }

    @Override
    public Void call() {
        System.out.println("Task " + taskNo + " started");
        while (!interrupted) {
            // Loop until interrupt detected

            // Wait for a while
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    // interrupt detected by means of an exception.
                    interrupted = true;
                    System.out.println(
                            "Task " + taskNo + " caught InterruptedException");
                    continue;
                }
            }

            // Do some work
            for (int i = 0; i < 100000000; i++) {
                // detect interrupts by explicit call.
                // Note that you only can get an InterruptedException when doing
                // a wait().
                if (Thread.interrupted()) {
                    System.out.println("Task " + taskNo
                            + ": interrupt detected, i = " + i);
                    interrupted = true;
                    break;
                }
            }
        }
        return null;
    }
}
