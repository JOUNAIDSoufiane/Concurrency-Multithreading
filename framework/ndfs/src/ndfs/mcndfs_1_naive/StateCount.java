package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import graph.State;

public class StateCount{ // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final Map<State, Integer> map = new HashMap<State, Integer>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static StateCount getInstance(){
        return singleInstance;
    }


    public void countIncrement(State s){
    	readWriteLock.writeLock().lock();
        if (map.get(s) == null) {
                map.put(s, 1);
        } else {
            int count = map.get(s);
            count++;
            map.put(s, count);
        }
        readWriteLock.writeLock().unlock();
    }

    public void countDecrement(State s){
    	readWriteLock.writeLock().lock();
        if (map.get(s) == 1) {
            map.remove(s);
        } else {
            int count = map.get(s);
            count--;
            map.put(s, count);
        }
        readWriteLock.writeLock().unlock();
    }

    public boolean isZero(State s){
    	boolean result;
    	readWriteLock.readLock().lock();
        result = map.get(s) == null;
        readWriteLock.readLock().unlock();
        return result;
    }
}