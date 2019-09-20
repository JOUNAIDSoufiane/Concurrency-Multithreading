package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;

import graph.State;

public class StateCount{ // Singleton class, to be used as a shared object

    private static StateCount singleInstance = new StateCount();
    private static final Map<State, Integer> map = new HashMap<State, Integer>();

    public static StateCount getInstance(){
        return singleInstance;
    }


    public void countIncrement(State s){
        if (map.get(s) == null)
                map.put(s, 1);
        else{
            int count = map.get(s);
            count++;
            map.put(s, count);
        }
    }

    public void countDecrement(State s){
        if (map.get(s) == 1)
            map.remove(s);
        else{
            int count = map.get(s);
            count--;
            map.put(s, count);
        }
    }

    public boolean isZero(State s){
        return map.get(s) == null;
    }
}