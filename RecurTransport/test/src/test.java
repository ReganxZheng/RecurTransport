/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;

/**
 *
 * @author ZhengJwh
 */
public class test {
    public static ArrayList<Driver> driver_list = new ArrayList<Driver>();
    
    public static void sortList() {
        Driver temp;
        for(int i=0;i<driver_list.size();i++) {
            for(int j=1;j<driver_list.size();j++) {
                if(driver_list.get(j).getPriority()<driver_list.get(j-1).getPriority()) {
                    temp = driver_list.get(j-1);
                    driver_list.set(j-1, driver_list.get(j));
                    driver_list.set(j,temp);
                }
            }
        }
    }
    public static void main(String[] args) {
        driver_list.add(new Driver("Frankie Zheng", "REC001", 1));
        driver_list.add(new Driver("Webber Jiang", "REC002",4));
        driver_list.add(new Driver("Charlie Ye","REC003", 3));
        driver_list.add(new Driver("Antony Li", "REC004", 2));
        
        sortList();
        System.out.println(driver_list.toString());
    }
   
    
    public static void swap(int a, int b) {
        
    }
}
