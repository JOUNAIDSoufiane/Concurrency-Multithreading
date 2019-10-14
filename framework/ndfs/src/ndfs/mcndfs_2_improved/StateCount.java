package ndfs.mcndfs_2_improved;

import java.util.concurrent.ConcurrentHashMap;


import graph.State;

public class StateCount{ // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final ConcurrentHashMap<State, Counter> map = new ConcurrentHashMap<State, Counter>();


    public static StateCount getInstance(){
        return singleInstance;
    }

    public void countIncrement(State s){ 
    	map.compute(s, (k, v) -> v == null ? new Counter(1) : v.incrementAndGet()); // executes atomically
    }

    public void countDecrement(State s){
    	map.compute(s, (k, v) -> v.get() == 1 ? map.remove(s) : v.decrementAndGet()); // executes atomically
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