/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author
 */
public class DriverTest {
    /**
     * Test of getLocation method, of class Driver.
     */
    @Test
    public void testGetLocation() {
        System.out.println("getLocation");
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        String expResult = "location";
        String result = instance.getLocation();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLocation method, of class Driver.
     */
    @Test
    public void testSetLocation() {
        System.out.println("setLocation");
        String expResult = "new location";
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.setLocation(expResult);
        String result = instance.getLocation();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRoundNumber method, of class Driver.
     */
    @Test
    public void testGetRoundNumber() {
        System.out.println("getRoundNumber");
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        int expResult = 0;
        int result = instance.getRoundNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of resetRounds method, of class Driver.
     */
    @Test
    public void testResetRounds() {
        System.out.println("resetRounds");
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.resetRounds();
        assertEquals(0, instance.getRounds().size());
    }

    /**
     * Test of increaseRound method, of class Driver.
     */
    @Test
    public void testIncreaseRound() {
        System.out.println("increaseRound");
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.increaseRound();
        assertEquals(1, instance.getRoundNumber());

    }

    /**
     * Test of setName method, of class Driver.
     */
    @Test
    public void testSetName() {
        System.out.println("setName");
        String name = "new name";
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.setName(name);
        assertEquals(name, instance.getName());

    }

    /**
     * Test of setID method, of class Driver.
     */
    @Test
    public void testSetID() {
        System.out.println("setID");
        String id = "new id";
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.setID(id);
        assertEquals(id, instance.getId());

    }

    /**
     * Test of setProirity method, of class Driver.
     */
    @Test
    public void testSetProirity() {
        System.out.println("setProirity");
        int priority = 10;
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        instance.setProirity(priority);
        assertEquals(priority, instance.getPriority());
    }

    /**
     * Test of getName method, of class Driver.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        String expResult = "name";
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        String result = instance.getName();
        assertEquals(expResult, instance.getName());
    }

    /**
     * Test of getId method, of class Driver.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        String expResult = "id";
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        String result = instance.getId();
        assertEquals(expResult, instance.getId());
    }

    /**
     * Test of getPriority method, of class Driver.
     */
    @Test
    public void testGetPriority() {
        System.out.println("getPriority");
        int expResult = 1;
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        int result = instance.getPriority();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Driver.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Driver instance = new Driver("name", "id", "location", 1, "07:00", "22:00");
        String expResult = "Priority: 1, Name: name, Id: id, Location: location";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
