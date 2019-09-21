package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private final Thread[] threads;
    private final Worker[] workers;

    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int nrWorkers) throws FileNotFoundException {
        workers = new Worker[nrWorkers];
        threads = new Thread[nrWorkers];
        for (int i = 0; i < nrWorkers; i++) {
            workers[i] = new Worker(promelaFile, nrWorkers, i);
            threads[i] = new Thread(workers[i]);
        }
    }

    @Override
    public boolean ndfs() {
        for (Thread t: threads) {
            t.start();
        }

        for (Thread t: threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                return true;
            }
        }

        for(int i=0; i < workers.length; i++){
            if (workers[i].getResult())
                return true;
        }
        return false;
    }
}
