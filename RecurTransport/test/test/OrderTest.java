/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;
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
public class OrderTest {
    /**
     * Test of getFrom method, of class Order.
     */
    @Test
    public void testGetFrom() {
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        System.out.println("getFrom");
        Order instance = new Order("id", time, size, "location", from, to);
        Location expResult = from;
        Location result = instance.getFrom();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTo method, of class Order.
     */
    @Test
    public void testGetTo() {
        System.out.println("getTo");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        Location result = instance.getTo();
        assertEquals(to, result);
    }

    /**
     * Test of setId method, of class Order.
     */
    @Test
    public void testSetId() {
        System.out.println("setId");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        instance.setId("new id");
        assertEquals("new id", instance.getId());
    }

    /**
     * Test of setTime method, of class Order.
     */
    @Test
    public void testSetTime() {
        System.out.println("setTime");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", null, size, "location", from, to);
        instance.setTime(time);
        assertEquals(time.formatCutOff(), instance.getTime());
    }

    /**
     * Test of getId method, of class Order.
     */
    @Test
    public void testGetId() {
        System.out.println("getId");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", null, size, "location", from, to);
        assertEquals("id", instance.getId());
    }

    /**
     * Test of getContainer method, of class Order.
     */
    @Test
    public void testGetContainer() {
        System.out.println("getContainer");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        ContainerList result = instance.getContainer();
        assertEquals(size, result);
    }

    /**
     * Test of getTime method, of class Order.
     */
    @Test
    public void testGetTime() {
        System.out.println("getTime");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        String result = instance.getTime();
        assertEquals(time.formatCutOff(), result);
    }

    /**
     * Test of getContainerSize method, of class Order.
     */
    @Test
    public void testGetContainerSize() {
        System.out.println("getContainerSize");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        String result = instance.getContainerSize();
        assertEquals(size.toString(), result);
    }

    /**
     * Test of setAddress method, of class Order.
     */
    @Test
    public void testSetAddress() {
        System.out.println("setAddress");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        instance.setAddress("new location");
        assertEquals("new location", instance.getAddressId());

    }

    /**
     * Test of getAddressId method, of class Order.
     */
    @Test
    public void testGetAddressId() {
        System.out.println("getAddressId");
        ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to);
        String result = instance.getAddressId();
        assertEquals("location", result);
    }

    /**
     * Test of toString method, of class Order.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
ContainerList size = new ContainerList(2, 2);
        Time time = new Time(2019, 6, 1, 7, 30, 0);
        Location from = new Location("from", "1");
        Location to = new Location("to", "2");
        Order instance = new Order("id", time, size, "location", from, to); 
        String expResult = "Order id: id. " + time.toString()
                + ". " + size.toString() + ". Address: location. From: " + from + ". To: " + to;
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
}
