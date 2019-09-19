package ndfs.mcndfs_1_naive;

import java.io.File;
import java.io.FileNotFoundException;

import graph.Graph;
import graph.GraphFactory;
import graph.State;

/**
 * This is a straightforward implementation of Figure 1 of
 * <a href="http://www.cs.vu.nl/~tcs/cm/ndfs/laarman.pdf"> "the Laarman
 * paper"</a>.
 */
public class Worker {

    private final Graph graph;
    private final Colors colors = new Colors();
    private boolean result = false;

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
    public Worker(File promelaFile) throws FileNotFoundException {

        this.graph = GraphFactory.createGraph(promelaFile);
    }

    private void dfsRed(State s) throws CycleFoundException {
        // s.pink = true
        for (State t : graph.post(s)) {
            if (colors.hasColor(t, Color.CYAN)) {
                throw new CycleFoundException();
            } else if (colors.hasColor(t, Color.BLUE)) { // if (!s.pink & !(sharedColors.hasColor(t, Color.RED)) )
                colors.color(t, Color.RED);              //     dfsRed(t)
                dfsRed(t);
            }
        }
        // if (s.isAccepting())
        //      s.count = s.count - 1  this is a shared variable and needs to be protected from concurrent access
        //      await s.count == 0
        // sharedColors.color(s,Color.RED)
        // s.pink = false
    }

    private void dfsBlue(State s) throws CycleFoundException {

        colors.color(s, Color.CYAN);
        for (State t : graph.post(s)) {
            if (colors.hasColor(t, Color.WHITE)) { // if(colors.hasColor(t, Color.WHITE) & (!(sharedColors.hasColor(t, Color.RED))) shared colors needs to be a shared Colors object between threads
                dfsBlue(t);
            }
        }
        if (s.isAccepting()) {
            // s.count = s.count + 1, this is a shared variable and needs to be protected from concurrent access
            dfsRed(s);
            colors.color(s, Color.RED);
        } else { // else statement to be removed
            colors.color(s, Color.BLUE);
        }
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
