package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private final Thread[] threads;
    private final Worker[] workers;
    ExecutorService executorService;
    CompletionService<Worker> executerCompletionService;
    

    /**
     * Constructs an NDFS object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public NNDFS(File promelaFile, int nrWorkers) throws FileNotFoundException {
    	executorService = Executors.newFixedThreadPool(nrWorkers);
    	executerCompletionService = new ExecutorCompletionService<Worker>(executorService);
   	 
        workers = new Worker[nrWorkers];
        threads = new Thread[nrWorkers];
        for (int i = 0; i < nrWorkers; i++) {
            workers[i] = new Worker(promelaFile,i);
            executerCompletionService.submit(workers[i]);
            //threads[i] = new Thread(workers[i]);
        }
    	
    }

    @Override
    public boolean ndfs() {
        for (int i = 0; i < workers.length; ++i) {
            try {
                Boolean result = false;
				try {
					result = executerCompletionService.take().get().getResult();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if (result) {
                	//Will interrupt all threads still running and shut down
                	executorService.shutdownNow();
                	return true;
                }
            } catch (ExecutionException ignore) {}
        }
    	 executorService.shutdown();
    	 return false;
    }
}
