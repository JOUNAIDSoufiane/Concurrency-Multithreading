package ndfs.mcndfs_2_naive;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import graph.State;

public class StateCount{ // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final ConcurrentHashMap<State, AtomicInteger> map = new ConcurrentHashMap<State, AtomicInteger>();
    public static AtomicInteger count = new AtomicInteger(0);

    public static StateCount getInstance(){
        return singleInstance;
    }


    public void countIncrement(State s){
        if (map.get(s) == null) {
                map.put(s, new AtomicInteger(1));
        } else {
            map.put(s, new AtomicInteger(map.get(s).incrementAndGet()));
        }
    }

    public void countDecrement(State s){
        if (map.get(s).get() == 1) {
            map.remove(s);
        } else {
            map.put(s, new AtomicInteger(map.get(s).decrementAndGet()));
        }
    }

    public boolean isZero(State s){
        return map.get(s) == null;
    }

    public int getCounter(State s) {
        if (map.containsKey(s))
            return map.get(s).get();
        else
            return -10;
    }
}