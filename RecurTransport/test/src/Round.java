
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ben
 */
public class Round {
    // what orders to dispatch in this round
    //              order id        size     count
    private HashMap<String, HashMap<Integer, Integer>> orderDispatch;
    private String from; // from locationID
    private String to; // to locationID
    
    public Round(){
        orderDispatch = new HashMap<String, HashMap<Integer, Integer>>();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public HashMap<String, HashMap<Integer, Integer>> getOrderDispatch(){
        return this.orderDispatch;
    }
    
    public boolean addContainer(String order_id, String from, String to, int size) {
        if (this.getCapacityLeft() >= size) {
            // create order if not exist
            if (!this.orderDispatch.containsKey(order_id)) {
                this.orderDispatch.put(order_id, new HashMap<Integer, Integer>());
            }

            // create size if not exist
            if (!this.orderDispatch.get(order_id).containsKey(size)) {
                this.orderDispatch.get(order_id).put(size, 0);
            }

            // increment count for container size
            this.orderDispatch.get(order_id).put(size, this.orderDispatch.get(order_id).get(size) + 1);
            this.from = from;
            this.to = to;
            
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty(){
        if(this.getCapacityLeft() == 60){
            return true;
        }else{
            return false;
        }
    }
    public int getCapacityLeft() {
        return 60 - this.getCapacity();
    }

    public int getCapacity() {
        HashMap<String, HashMap<Integer, Integer>> roundSchedule = orderDispatch;
        if (roundSchedule.isEmpty()) {
            return 0;
        } else {
            int total = 0;
            for (HashMap<Integer, Integer> entry : roundSchedule.values()) {
                total += this.getOrderCapacity(entry);
            }
            return total;
        }
    }

    public int getOrderCapacity(HashMap<Integer, Integer> order) {
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : order.entrySet()) {
            Integer size = entry.getKey();
            Integer count = entry.getValue();
            total += size * count;
        }
        return total;
    }
}
