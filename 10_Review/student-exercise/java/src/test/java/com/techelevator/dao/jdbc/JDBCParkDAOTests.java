package com.techelevator.dao.jdbc;

import com.techelevator.dao.ParkDAO;
import com.techelevator.model.Park;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JDBCParkDAOTests extends BaseDAOTests {

    private ParkDAO dao;

    @Before
    public void setup() {
        dao = new JDBCParkDAO(dataSource);
    }

    @Test
    public void getParksTest_Should_ReturnAllParksInLocationAlphaOrder() {
        List<Park> parks = dao.getAllParks();

        assertEquals(3, parks.size());
        assertEquals("Maine", parks.get(0).getLocation());
        assertEquals("Ohio", parks.get(1).getLocation());
        assertEquals("Utah", parks.get(2).getLocation());
    }

}
