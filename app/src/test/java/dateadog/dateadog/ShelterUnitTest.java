package dateadog.dateadog;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * This class contains a set of test cases that can be used to test the
 * implementation of the Shelter class.
 */
public class ShelterUnitTest {

    private Shelter shelter1;
    private Shelter shelter2 = null;


    @Before
    public void setUp() throws Exception {
        // setting up new Shelter
        shelter1 = new Shelter("3730","989", "Seattle", "WA", 98108, "2068972310" , "shelter1@gmail.com", "Seattle Shelter" );

    }

    @Test
    public void testConstructor() throws Exception {

        // testing whether constructor is null or not
        assertEquals(shelter2, null);
        assertNotEquals(shelter1, null);
    }

    @Test
    public void testGetShelterId() throws Exception {

        // testing getShelterId which returns shelter Id
        assertEquals(shelter1.getShelterId(), "989");
        assertNotEquals(shelter1.getShelterId(), "231");
    }

    @Test
    public void testGetStreet() throws Exception {

        // testing getStreet which returns shelter street
        assertEquals(shelter1.getStreet(), "3730");
        assertNotEquals(shelter1.getStreet(), "231");
    }

    @Test
    public void testGetCity() throws Exception {

        // testing getCity which returns shelter's city
        assertEquals(shelter1.getCity(), "Seattle");
        assertNotEquals(shelter1.getCity(), "New York");
    }

    @Test
    public void testGetState() throws Exception {

        // testing getState which returns shelter's state
        assertEquals(shelter1.getState(), "WA");
        assertNotEquals(shelter1.getState(), "NY");
    }

    @Test
    public void testGetZip() throws Exception {

        // testing getZip which returns shelter's zip code
        assertEquals(shelter1.getZip(), 98108);
        assertNotEquals(shelter1.getZip(), 98112);
    }

    @Test
    public void testGetPhone() throws Exception {

        // testing getPhone which returns shelter's phone number
        assertEquals(shelter1.getPhone(), "2068972310");
        assertNotEquals(shelter1.getPhone(), "2068972365");
    }

    @Test
    public void testGetEmail() throws Exception {

        // testing getEmail which returns shelter's email
        assertEquals(shelter1.getEmail(), "shelter1@gmail.com");
        assertNotEquals(shelter1.getEmail(), "shelter1@yahoo.com");
    }

    @Test
    public void testGetName() throws Exception {

        // testing getName which returns shelter's name
        assertEquals(shelter1.getName(), "Seattle Shelter");
        assertNotEquals(shelter1.getName(), "New York Shelter");
    }

}