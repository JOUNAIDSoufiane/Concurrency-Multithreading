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
        pinkMap.put(s, true);
        List<State> list = graph.post(s);
        for (int i = nrWorker; i < list.size(); i += nrThreads) {
            if (colors.hasColor(list.get(i), Color.CYAN)) {
                throw new CycleFoundException();
            } else if (pinkMap.get(s) == null && !SharedColors.getInstance().isRed(s) ) {
                dfsRed(list.get(i));
            }
        }
         if (s.isAccepting()){
              StateCount.getInstance().countDecrement(s); // Critical section
              while (!StateCount.getInstance().isZero(s)) {}
         }
        SharedColors.getInstance().setRed(s);
        pinkMap.remove(s);
    }

    private void dfsBlue(State s) throws CycleFoundException {

        colors.color(s, Color.CYAN);
        List<State> list = graph.post(s);
        for (int i = nrWorker; i < list.size(); i = i + nrThreads) {
            //System.out.println( "Thread "+ threadnumber +" state returned : " + s);
            if (colors.hasColor(list.get(i), Color.WHITE) && (!SharedColors.getInstance().isRed(s)) ) {
                dfsBlue(list.get(i));
            }
        }
        if (s.isAccepting()) {
            StateCount.getInstance().countIncrement(s); // CS : needs to be protected from concurrent access
            dfsRed(s);
        }
        colors.color(s, Color.BLUE);
    }

    private void nndfs(State s) throws CycleFoundException {
        dfsBlue(s);
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
