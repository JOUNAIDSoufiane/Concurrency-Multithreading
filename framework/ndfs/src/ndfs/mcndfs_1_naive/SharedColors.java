package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import graph.State;

public class SharedColors {

    private static SharedColors singleInstance = new SharedColors();
    private static final Map<State, Boolean> map = new HashMap<State, Boolean>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static SharedColors getInstance(){
        return singleInstance;
    }

    public boolean isRed(State s){
    	boolean result;
    	readWriteLock.readLock().lock();
        result = map.get(s) != null;
        readWriteLock.readLock().unlock();
        return result;
    }

    public void setRed(State s){
    	readWriteLock.writeLock().lock();
        map.put(s, true);
        readWriteLock.writeLock().unlock();
    }
}