package ndfs;

/**
 * This interface specifies the way this framework calls an NDFS implementation
 * and the way the result is passed.
 */
public interface NDFS {

    /**
     * This method determines whether or not the graph has an accepting cycle,
     * and returns a boolean indicating the result.
     *
     * @return whether the graph contains an accepting cycle.
     */
    public boolean ndfs();
}
