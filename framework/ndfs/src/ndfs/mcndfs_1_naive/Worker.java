package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker implements Runnable {

    private final Graph graph;
    private final Colors colors = new Colors();

    private boolean result = false;

    private int nrThreads;
    private int nrWorker;


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
    public Worker(File promelaFile, int nrThreads, int nrWorker) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
        this.nrThreads = nrThreads;
        this.nrWorker = nrWorker;
    }

    private void dfsRed(State s) throws CycleFoundException {
//        System.out.println( "Thread "+ nrWorker +" current state in dfs red : " + s);
        pinkMap.put(s, true);
        //  Post permuation
        for (State t : graph.post(s)) {
//            System.out.println( "Thread "+ nrWorker +" state returned dfs red : " + list.get(i));
            if (colors.hasColor(t, Color.CYAN)) {
                throw new CycleFoundException();
            } else if (pinkMap.get(s) == null && !SharedColors.getInstance().isRed(s) ) {
//                System.out.println( "Thread "+ nrWorker +" initiating dfs red on succesor : " + list.get(i));
                dfsRed(t);
            }
        }
         if (s.isAccepting()){
        	 synchronized(StateCount.getInstance()) {
        		 StateCount.getInstance().countDecrement(s); // Critical section
        	 }
             while (!StateCount.getInstance().isZero(s)) {}
         }
//         System.out.println( "Thread "+ nrWorker +" colored red state : " + s);
         SharedColors.getInstance().setRed(s);
         pinkMap.remove(s);
//         System.out.println( "Thread "+ nrWorker +" leaving dfs red from " + s);
    }

    private void dfsBlue(State s) throws CycleFoundException {
       // System.out.println( "Thread "+ nrWorker +" current state in dfs blue : " + s);
        colors.color(s, Color.CYAN);
        //  Post permuation
        for (State t : graph.post(s)) { // i += nrThreads skips succesors on backtracking
            if (colors.hasColor(t, Color.WHITE) && (!SharedColors.getInstance().isRed(s)) ) {
//                System.out.println( "Thread "+ nrWorker +" initiating dfs blue on succesor : " + list.get(i));
                dfsBlue(t);
//                System.out.println( "Thread "+ nrWorker +" backtracking from : " + list.get(i));
            }
        }
        if (s.isAccepting()) {
        	synchronized(StateCount.getInstance()) {
        		StateCount.getInstance().countIncrement(s); // CS : needs to be protected from concurrent access
        	}
//            System.out.println( "Thread "+ nrWorker +" initiating dfs red on " + s);
            dfsRed(s);
        }
        colors.color(s, Color.BLUE);
    }

    private void nndfs(State s) throws CycleFoundException {
    	List<State> list = graph.post(s);
    	
    	//Gives each thread a successor of the initial node until there are no more successors
		int i;
		if(StateCount.count.get() >= list.size())
			list = graph.post(list.get(list.size() - 1));
    	while ((i = StateCount.count.getAndIncrement()) < list.size()) {
    		dfsBlue(list.get(i));
    	}
    }

    public void run() {
        try {
            nndfs(graph.getInitialState());
        } catch (CycleFoundException e) {
            result = true;
        }
    }

    public boolean getResult() {
        return result;
    }
}
