package model;

import exceptions.NotInStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {
    private Commodity commodity;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
        commodity.setInStock(10);
        commodity.setInitRate(1);
    }

    @Test
    public void testUpdateInStock() {
        assertDoesNotThrow(() -> commodity.updateInStock(5));
        assertEquals(15, commodity.getInStock());

        assertThrows(NotInStock.class, () -> commodity.updateInStock(-20));
    }

    @Test
    public void testAddRate() {
        commodity.addRate("user1", 5);
        assertEquals(3.0, commodity.getRating());

        commodity.addRate("user2", 3);
        assertEquals(3.0, commodity.getRating());
    }
}