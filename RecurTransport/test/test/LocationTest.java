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
public class LocationTest {
    
    /**
     * Test of getLocationName method, of class Location.
     */
    @Test
    public void testGetLocationName() {
        System.out.println("getLocationName");
        Location instance = new Location("name", "id");
        String expResult = "name";
        String result = instance.getLocationName();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLocationName method, of class Location.
     */
    @Test
    public void testSetLocationName() {
        System.out.println("setLocationName");
        String locationName = "new name";
        Location instance = new Location("name", "id");
        instance.setLocationName(locationName);
        String result = instance.getLocationName();
        assertEquals(locationName, result);
    }

    /**
     * Test of getLocationID method, of class Location.
     */
    @Test
    public void testGetLocationID() {
        System.out.println("getLocationID");
        Location instance = new Location("name", "id");
        String expResult = "id";
        String result = instance.getLocationID();
        assertEquals(expResult, result);
    }

    /**
     * Test of setLocationID method, of class Location.
     */
    @Test
    public void testSetLocationID() {
        System.out.println("setLocationID");
        String locationID = "new id";
        Location instance = new Location("name", "id");
        instance.setLocationID(locationID);
        String result = instance.getLocationID();
        assertEquals(locationID, result);
    }

    /**
     * Test of toString method, of class Location.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Location instance = new Location("name", "id");
        String expResult = "name(id)";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of equals method, of class Location.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Location instance = new Location("name", "id");
        Location instance2 = new Location("name", "id");
        boolean expResult = true;
        boolean result = instance.equals(instance2);
        assertEquals(expResult, result);
    }
    
}
