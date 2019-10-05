package ndfs.mcndfs_2_naive;

import java.util.concurrent.ConcurrentHashMap;

import graph.State;

public class SharedColors {

    private static SharedColors singleInstance = new SharedColors();
    private static final ConcurrentHashMap<State, Boolean> map = new ConcurrentHashMap<State, Boolean>();

    public static SharedColors getInstance(){
        return singleInstance;
    }

    public boolean isRed(State s){
    	return (map.get(s) != null);
    }

    public void setRed(State s){
        map.put(s, true);
    }
}