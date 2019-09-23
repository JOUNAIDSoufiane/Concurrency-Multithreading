package ndfs.mcndfs_1_naive;

import java.util.concurrent.locks.ReentrantLock;

public class SharedLock extends ReentrantLock{

    public static ReentrantLock lock = new ReentrantLock();

}