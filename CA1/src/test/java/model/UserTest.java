package model;

import exceptions.CommodityIsNotInBuyList;
import exceptions.InsufficientCredit;
import exceptions.InvalidCreditRange;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class UserTest {
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("Username", "Password", "Email", "BirthDate", "Address");
    }

    @Test
    void testAddCredit() throws InvalidCreditRange {
        user.addCredit(500);
        assertEquals(500, user.getCredit(), "Credit should be 500");
    }

    @Test
    void testAddCreditNegativeAmount() {
        assertThrows(InvalidCreditRange.class, () -> user.addCredit(-500), "Should throw InvalidCreditRange");
    }

    @Test
    void testWithdrawCredit() throws InsufficientCredit {
        user.setCredit(1000);
        user.withdrawCredit(500);
        assertEquals(500, user.getCredit(), "Credit should be 500 after withdrawal");
    }

    @Test
    void testWithdrawCreditInsufficient() {
        user.setCredit(100);
        assertDoesNotThrow(() -> user.withdrawCredit(100), "Shouldn't throw InsufficientCredit");
        assertThrows(InsufficientCredit.class, () -> user.withdrawCredit(100.00009F), "Should throw InsufficientCredit");
    }

    @Test
    void testAddBuyItem() {
        Commodity commodity = new Commodity();
        user.addBuyItem(commodity);
        assertEquals(1, user.getBuyList().get(commodity.getId()).intValue(), "Should have 1 item in buy list");
    }

    @Test
    void testAddPurchasedItem() {
        user.addPurchasedItem("1", 5);
        assertEquals(5, user.getPurchasedList().get("1").intValue(), "Should have 5 items with id 1 in purchased list");
    }

    @Test
    void testRemoveItemFromBuyListWithOneItem() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertFalse(user.getBuyList().containsKey(commodity.getId()), "Should not have item in buy list");
    }

    void testRemoveItemFromBuyListWithMoreThanOneItem() throws CommodityIsNotInBuyList {
        Commodity commodity = new Commodity();
        user.addBuyItem(commodity);
        user.addBuyItem(commodity);
        user.removeItemFromBuyList(commodity);
        assertTrue(1, user.getBuyList().get(commodity.getId()).intValue(), "Should have remained 1 item in buy list");
    }

    @Test
    void testRemoveItemFromBuyListNotInList() {
        Commodity commodity = new Commodity();
        assertThrows(CommodityIsNotInBuyList.class, () -> user.removeItemFromBuyList(commodity), "Should throw CommodityIsNotInBuyList");
    }
}
