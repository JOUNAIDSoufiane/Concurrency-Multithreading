package ndfs.mcndfs_2_improved;

import java.util.concurrent.ConcurrentHashMap;


import graph.State;

public class StateCount{ // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final ConcurrentHashMap<State, Counter> map = new ConcurrentHashMap<State, Counter>();


    public static StateCount getInstance(){
        return singleInstance;
    }

//// MUTUAL EXCLUSION FIX ////
//    public void countIncrement(State s){ 
//        if (map.get(s) == null) {
//                map.put(s, new Counter(1));
//        } else {
//            map.put(s, map.get(s).incrementAndGet());
//        }
//    }
    public void countIncrement(State s){ 
    	map.compute(s, (k, v) -> v == null ? new Counter(1) : v.incrementAndGet());
    }

//    public void countDecrement(State s){
//        if (map.get(s).get() == 1) {
//            map.remove(s);
//        } else {
//            map.put(s, map.get(s).decrementAndGet());
//        }
//    }
    public void countDecrement(State s){
    	map.compute(s, (k, v) -> v.get() == 1 ? map.remove(s) : v.decrementAndGet());
    }
//// MUTUAL EXCLUSION FIX ////
    
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