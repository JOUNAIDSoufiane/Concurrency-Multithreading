package driver;

import java.io.File;
import java.io.FileNotFoundException;

import ndfs.NDFS;
import ndfs.NDFSFactory;

/**
 * This is the main program of the NDFS skeleton.
 */
public class Main {

    // Prevent accidental construction.
    private Main() {
        // nothing
    }

    private static void printUsage() {
        System.out.println("Usage: bin/ndfs <file> <version> <nrWorkers>");
        System.out.println("  where");
        System.out.println("    <file> is a Promela file (.prom),");
        System.out.println(
                "    <version> is either \"seq\" or one of the mc versions (for instance, \"1_naive\"),");
        System.out.println(
                "    <nWorkers> indicates the number of worker threads.");
    }

    private static void runNDFS(File promelaFile) throws FileNotFoundException {

        NDFS ndfs = NDFSFactory.createNNDFS(promelaFile);
        long start = System.currentTimeMillis();

        boolean result = ndfs.ndfs();
        long end = System.currentTimeMillis();
        System.out.println("Graph " + promelaFile.getName() + " does "
                + (result ? "" : "not ") + "contain an accepting cycle.");
        System.out.println("seq took " + (end - start) + " ms.");
    }

    private static void runMCNDFS(File promelaFile, String version,
            int nrWorkers) throws Exception {

        NDFS ndfs = NDFSFactory.createMCNDFS(promelaFile, nrWorkers, version);
        long start = System.currentTimeMillis();

        boolean result = ndfs.ndfs();
        long end = System.currentTimeMillis();
        System.out.println("Graph " + promelaFile.getName() + " does "
                + (result ? "" : "not ") + "contain an accepting cycle.");
        System.out.println(version + " took " + (end - start) + " ms.");
    }

    private static void dispatch(File promelaFile, String version,
            int nrWorkers)
            throws IllegalArgumentException, FileNotFoundException {

        if (version.equals("seq")) {
            if (nrWorkers != 1) {
                throw new IllegalArgumentException(
                        "seq can only run with 1 worker");
            }
            runNDFS(promelaFile);
        } else {
            try {
                runMCNDFS(promelaFile, version, nrWorkers);
            } catch (FileNotFoundException e) {
                throw e;
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Throwable e) {
                throw new Error("Could not run version " + version + ": " + e,
                        e);
            }
        }
    }

    /**
     * This is the <code>main</code> method of the NDFS skeleton. It takes three
     * arguments: <br>
     * - a filename of a file containing a Promela program; this describes the
     * graph. <br>
     * - a version string, see
     * {@link NDFSFactory#createMCNDFS(File, int, String)}, but it could also be
     * "seq", for the sequential version. <br>
     * - a number representing the number of worker threads to be used.
     *
     * @param argv
     *            the arguments.
     */
    public static void main(String[] argv) {
        try {
            if (argv.length != 3)
                throw new IllegalArgumentException("Wrong number of arguments");
            File file = new File(argv[0]);
            String version = argv[1];
            int nrWorkers = new Integer(argv[2]);

            dispatch(file, version, nrWorkers);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            printUsage();
        }
    }
}
