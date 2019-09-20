package ndfs.mcndfs_1_naive;

import java.util.HashMap;
import java.util.Map;

import graph.State;

public class SharedColors {

    private static SharedColors singleInstance = new SharedColors();
    private static final Map<State, Boolean> map = new HashMap<State, Boolean>();

    public static SharedColors getInstance(){
        return singleInstance;
    }

    public boolean isRed(State s){
        return map.get(s) != null;
    }

    public boolean setRed(State s){
        map.put(s, true);
    }
}