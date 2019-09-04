package ndfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;

/**
 * This factory class is responsible for creating NDFS objects.
 */
public class NDFSFactory {

    // Prevent accidental construction.
    private NDFSFactory() {
        // nothing
    }

    /**
     * This method creates a sequential NDFS object using the specified Promela
     * file.
     *
     * @param promelaFile
     *            the Promela file specifying the graph.
     * @return the NDFS object which implements sequential NDFS.
     * @throws FileNotFoundException
     *             thrown in case the specified Promela file could not be read.
     */
    public static NDFS createNNDFS(File promelaFile)
            throws FileNotFoundException {
        return new ndfs.nndfs.NNDFS(promelaFile);
    }

    /**
     * This method creates an NDFS object using Java introspection. The
     * assumption here is that the class name of the NDFS object can be
     * constructed as "ndfs.mcndfs_", followed by the specified version,
     * followed by ".NNDFS", for instance, if the version parameter is
     * "1_naive", we would get the class name "ndfs.mcndfs_1_naive.NNDFS". Also
     * assumed is that this class has a public constructor with two parameters:
     * a <code>File</code> and an integer, specifying the Promela file and the
     * number of workers, respectively.
     * <p>
     * Exception handling is extremely basic here (read: absent; everything is
     * passed through).
     *
     * @param promelaFile
     *            the Promela file specifying the graph.
     * @param nrWorkers
     *            the number of worker threads to be used.
     * @param version
     *            the specified version.
     * @return the NDFS object.
     * @throws Exception
     *             thrown in case something goes wrong.
     */
    public static NDFS createMCNDFS(File promelaFile, int nrWorkers,
            String version) throws Exception {

        String className = "ndfs.mcndfs_" + version + ".NNDFS";
        Class<?> mcndfsClass = Class.forName(className);
        Constructor<?> c = mcndfsClass.getConstructor(File.class, Integer.TYPE);
        return (NDFS) c.newInstance(promelaFile, nrWorkers);
    }
}
