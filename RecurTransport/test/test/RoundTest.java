/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
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
public class RoundTest {
    
    /**
     * Test of getFrom method, of class Round.
     */
    @Test
    public void testGetFrom() {
        System.out.println("getFrom");
        Round instance = new Round();
        String expResult = null;
        String result = instance.getFrom();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTo method, of class Round.
     */
    @Test
    public void testGetTo() {
        System.out.println("getTo");
        Round instance = new Round();
        String expResult = null;
        String result = instance.getTo();
        assertEquals(expResult, result);
    }

    /**
     * Test of addContainer method, of class Round.
     */
    @Test
    public void testAddContainer() {
        System.out.println("addContainer");
        String order_id = "id";
        String from = "from";
        String to = "to";
        int size = 0;
        Round instance = new Round();
        boolean expResult = true;
        boolean result = instance.addContainer(order_id, from, to, size);
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class Round.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Round instance = new Round();
        boolean expResult = true;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCapacityLeft method, of class Round.
     */
    @Test
    public void testGetCapacityLeft() {
        System.out.println("getCapacityLeft");
        Round instance = new Round();
        int expResult = 60;
        int result = instance.getCapacityLeft();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCapacity method, of class Round.
     */
    @Test
    public void testGetCapacity() {
        System.out.println("getCapacity");
        Round instance = new Round();
        int expResult = 0;
        int result = instance.getCapacity();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOrderCapacity method, of class Round.
     */
    @Test
    public void testGetOrderCapacity() {
        System.out.println("getOrderCapacity");
        HashMap<Integer, Integer> order = new HashMap<Integer, Integer>();
        Round instance = new Round();
        int expResult = 0;
        int result = instance.getOrderCapacity(order);
        assertEquals(expResult, result);
    }
    
}
