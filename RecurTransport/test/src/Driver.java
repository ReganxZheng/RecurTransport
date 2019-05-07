
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ZhengJwh
 */
public class Driver {

    protected String driver_name;
    protected String driver_id;
    protected String location;
    protected int driver_priority;
    protected String start_time;
    protected String stop_time;
    private int current_round;
    private ArrayList<Round> rounds;
    

    public Driver(String name, String id, int priority) {
        this(name, id, null, priority, "00:00", "23:00");
    }
    
    public Driver(String name, String id, String location, int priority, String start_time, String stop_time) {
        this.driver_name = name;
        this.driver_id = id;
        this.location = location;
        this.driver_priority = priority;
        this.current_round = 0;
        this.start_time = start_time;
        this.stop_time = stop_time;
        
        rounds = new ArrayList<Round>();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public int getRoundNumber(){
        return this.current_round;
    }

    public Round getRound(){
        if(this.rounds.size() <= current_round){
            this.rounds.add(new Round());
        }
        Round r = this.rounds.get(current_round);
        if(r == null){
            r = new Round();
            this.rounds.add(r);
        }
        return r;
    }
    
    public ArrayList<Round> getRounds(){
        return this.rounds;
    }
    
    public void resetRounds(){
        this.rounds.clear();
        this.current_round = 0;
    }
    
    public void increaseRound(){
        this.current_round++;
    }

    public void setName(String name) {
        this.driver_name = name;
    }

    public void setID(String id) {
        this.driver_id = id;
    }

    public void setProirity(int priority) {
        this.driver_priority = priority;
    }

    public String getName() {
        return this.driver_name;
    }

    public String getId() {
        return this.driver_id;
    }

    public int getPriority() {
        return this.driver_priority;
    }

    @Override
    public String toString() {
        return "Priority: " + driver_priority + ", Name: " + driver_name + ", Id: " + driver_id
                + ", Location: " + location;
    }
}
