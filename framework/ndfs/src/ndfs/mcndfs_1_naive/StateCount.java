package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import graph.State;

public class StateCount { // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final Map<State, Counter> map = new HashMap<State, Counter>();
    public static AtomicInteger count = new AtomicInteger(0);

    public static StateCount getInstance(){
        return singleInstance;
    }


    public void countIncrement(State s){
        if (map.get(s) == null) {
            map.put(s, new Counter(1));
        } else {
            map.put(s, map.get(s).incrementAndGet());
        }
    }

    public void countDecrement(State s){
        if (map.get(s).get() == 1) {
            map.remove(s);
        } else {
            map.put(s, map.get(s).decrementAndGet());
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