/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class ContainerListTest {
    
    /**
     * Test of setContainer20 method, of class ContainerList.
     */
    @Test
    public void testSetContainer20() {
        System.out.println("setContainer20");
        int num = 10;
        ContainerList instance = new ContainerList(1, 1);
        instance.setContainer20(num);
        assertEquals(10, instance.con20_num);
    }

    /**
     * Test of setContainer40 method, of class ContainerList.
     */
    @Test
    public void testSetContainer40() {
        System.out.println("setContainer40");
        int num = 10;
        ContainerList instance = new ContainerList(1, 1);
        instance.setContainer40(num);
        assertEquals(10, instance.con40_num);
    }

    /**
     * Test of getContainer20 method, of class ContainerList.
     */
    @Test
    public void testGetContainer20() {
        System.out.println("getContainer20");
        ContainerList instance = new ContainerList(10, 10);
        int expResult = 10;
        int result = instance.getContainer20();
        assertEquals(expResult, result);
    }

    /**
     * Test of getContainer40 method, of class ContainerList.
     */
    @Test
    public void testGetContainer40() {
        System.out.println("getContainer40");
        ContainerList instance = new ContainerList(10, 10);
        int expResult = 10;
        int result = instance.getContainer40();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class ContainerList.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        ContainerList instance = new ContainerList(10, 10);
        String expResult = "This order has <20ft-container> = [10]  <40ft-container> = [10]";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class ContainerList.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        ContainerList instance = new ContainerList(10, 10);
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }
    
}
