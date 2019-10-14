package ndfs.mcndfs_2_improved;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ndfs.NDFS;

/**
 * Implements the {@link ndfs.NDFS} interface, mostly delegating the work to a
 * worker class.
 */
public class NNDFS implements NDFS {

    private final Worker[] workers;
    ExecutorService executorService;
    CompletionService<Worker> executorCompletionService;
    

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
    	executorCompletionService = new ExecutorCompletionService<Worker>(executorService);
   	 
        workers = new Worker[nrWorkers];
        for (int i = 0; i < nrWorkers; i++) {
            workers[i] = new Worker(promelaFile,i);
            executorCompletionService.submit(workers[i]);
        }
    	
    }

    @Override
    public boolean ndfs() {
        for (int i = 0; i < workers.length; ++i) {
            try {
                Boolean result = false;
				try {
					result = executorCompletionService.take().get().getResult();
				} catch (InterruptedException ignore) {}
				//When one thread finds a cycle, all others are interrupted on executorService shutdown 
				//Therefore all interrupted Exceptions are ignored
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
