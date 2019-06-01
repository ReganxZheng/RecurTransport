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
public class TimeTest {
    
    /**
     * Test of setCutOffDate method, of class Time.
     */
    @Test
    public void testSetCutOffDate() {
        System.out.println("setCutOffDate");
        Date date = new Date(119,5,1, 7, 30, 0);;
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        instance.setCutOffDate(date);
        assertTrue(instance.getDate().equals(date));
    }

    /**
     * Test of getDate method, of class Time.
     */
    @Test
    public void testGetDate() {
        System.out.println("getDate");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        Date date = new Date(119,5,1, 7, 30, 0);;
        assertTrue(instance.getDate().equals(date));
    }

    /**
     * Test of formatCutOff method, of class Time.
     */
    @Test
    public void testFormatCutOff() {
        System.out.println("formatCutOff");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        String expResult = "01/06/2019 7:30:0";
        String result = instance.formatCutOff();
        assertEquals(expResult, result);
    }

    /**
     * Test of getYear method, of class Time.
     */
    @Test
    public void testGetYear() {
        System.out.println("getYear");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        int expResult = 2019;
        int result = instance.getYear();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMonth method, of class Time.
     */
    @Test
    public void testGetMonth() {
        System.out.println("getMonth");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        int expResult = 6;
        int result = instance.getMonth();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDay method, of class Time.
     */
    @Test
    public void testGetDay() {
        System.out.println("getDay");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        int expResult = 1;
        int result = instance.getDay();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHour method, of class Time.
     */
    @Test
    public void testGetHour() {
        System.out.println("getHour");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        int expResult = 7;
        int result = instance.getHour();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMin method, of class Time.
     */
    @Test
    public void testGetMin() {
        System.out.println("getMin");
        Time instance = new Time(2019, 6, 1, 7, 30, 0);
        int expResult = 30;
        int result = instance.getMin();
        assertEquals(expResult, result);
    }
    
}
