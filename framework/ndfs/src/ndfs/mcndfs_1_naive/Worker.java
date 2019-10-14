package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;
import ndfs.mcndfs_2_improved.StateCount;

import java.util.HashMap;
import java.util.Map;

import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.List;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker implements Callable<Worker> {

    private final Graph graph;
    private final Colors colors = new Colors();
    private final int threadnumber;

    private boolean result = false;

    private final Map<State,Boolean> pinkMap = new HashMap<State, Boolean>();

    // Throwing an exception is a convenient way to cut off the search in case a
    // cycle is found.
    private static class CycleFoundException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Constructs a Worker object using the specified Promela file.
     *
     * @param promelaFile
     *            the Promela file.
     * @throws FileNotFoundException
     *             is thrown in case the file could not be read.
     */
    public Worker(File promelaFile, int i) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
        this.threadnumber = i;
    }

    private void dfsRed(State s) throws CycleFoundException, InterruptedException {
        pinkMap.put(s, true);
        for (State t : perm(s)) {
            if (colors.hasColor(t, Color.CYAN)) {
                throw new CycleFoundException();
            } else {
                SharedLock.lock.lock();
            	if (pinkMap.get(t) == null && !SharedColors.getInstance().isRed(t) ) {
	                dfsRed(t);
	            }
                SharedLock.lock.unlock();
            }
        }
        if (s.isAccepting()){
        	SharedLock.lock.lock();
            StateCount.getInstance().countDecrement(s); // Critical section
            SharedLock.lock.unlock();
            synchronized(StateCount.getInstance()) {
				if (!StateCount.getInstance().isZero(s))
					StateCount.getInstance().wait();
				else 
					StateCount.getInstance().notifyAll();
			}
        }
        SharedLock.lock.lock();
        SharedColors.getInstance().setRed(s);
        SharedLock.lock.unlock();
        pinkMap.remove(s);
    }

    private void dfsBlue(State s) throws CycleFoundException, InterruptedException {
        colors.color(s, Color.CYAN);
        for (State t : perm(s)) {
            if (colors.hasColor(t, Color.WHITE) && (!SharedColors.getInstance().isRed(t)) ) {
                dfsBlue(t);
            }
        }
        if (s.isAccepting()) {
            SharedLock.lock.lock();
            StateCount.getInstance().countIncrement(s); // CS : needs to be protected from concurrent access
            SharedLock.lock.unlock();
            dfsRed(s);
        }
        colors.color(s, Color.BLUE);
    }

    private List<State> perm(State s) { // permutation function randomizes the order of succesors based on the thread number as a seed
        List<State> permutated = graph.post(s);
        Collections.shuffle(permutated, new Random(threadnumber));
        return permutated;
    }

    private void nndfs(State s) throws CycleFoundException, InterruptedException {
    	dfsBlue(s);
    }

    public boolean getResult() {
        return result;
    }

	@Override
	public Worker call() throws Exception {
		try {
            nndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            result = true;
        }
		return this;
	}
}