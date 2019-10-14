package ndfs.mcndfs_1_naive;

import java.util.concurrent.locks.ReentrantLock;

public class SharedLock extends ReentrantLock{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static ReentrantLock lock = new ReentrantLock();

}