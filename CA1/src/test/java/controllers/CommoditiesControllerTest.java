package controllers;

import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommoditiesControllerTest {

    private CommoditiesController controllerUnderTest;

    @Mock
    private Baloot mockBaloot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        controllerUnderTest = new CommoditiesController();
        controllerUnderTest.setBaloot(mockBaloot);
    }

    @Test
    void shouldReturnCommodities() {
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(mockBaloot.getCommodities()).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> result = controllerUnderTest.getCommodities();

        assertSame(expectedCommodities, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldReturnCommodity() throws NotExistentCommodity {
        String id = "1";
        Commodity expectedCommodity = new Commodity();
        when(mockBaloot.getCommodityById(id)).thenReturn(expectedCommodity);

        ResponseEntity<Commodity> result = controllerUnderTest.getCommodity(id);

        assertSame(expectedCommodity, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldRateCommodity() throws NotExistentCommodity {
        String id = "1";
        Map<String, String> input = Map.of("rate", "5", "username", "user1");
        Commodity mockCommodity = mock(Commodity.class);
        when(mockBaloot.getCommodityById(id)).thenReturn(mockCommodity);

        ResponseEntity<String> result = controllerUnderTest.rateCommodity(id, input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(mockCommodity).addRate(input.get("username"), Integer.parseInt(input.get("rate")));
    }

    @Test
    void shouldAddCommodityComment() throws NotExistentUser {
        String id = "1";
        Map<String, String> input = Map.of("username", "user1", "comment", "Nice commodity!");
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn(input.get("username"));
        when(mockUser.getEmail()).thenReturn("user1@example.com");
        when(mockBaloot.getUserById(input.get("username"))).thenReturn(mockUser);

        ResponseEntity<String> result = controllerUnderTest.addCommodityComment(id, input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(mockBaloot).addComment(any(Comment.class));
    }

    @Test
    void shouldGetCommodityComment() {
        String id = "1";
        ArrayList<Comment> expectedComments = new ArrayList<>();
        when(mockBaloot.getCommentsForCommodity(Integer.parseInt(id))).thenReturn(expectedComments);

        ResponseEntity<ArrayList<Comment>> result = controllerUnderTest.getCommodityComment(id);

        assertSame(expectedComments, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldSearchCommodities() {
        Map<String, String> input = Map.of("searchOption", "name", "searchValue", "commodity1");
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(mockBaloot.filterCommoditiesByName(input.get("searchValue"))).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> result = controllerUnderTest.searchCommodities(input);

        assertSame(expectedCommodities, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void shouldGetSuggestedCommodities() throws NotExistentCommodity {
        String id = "1";
        Commodity mockCommodity = new Commodity();
        ArrayList<Commodity> expectedCommodities = new ArrayList<>();
        when(mockBaloot.getCommodityById(id)).thenReturn(mockCommodity);
        when(mockBaloot.suggestSimilarCommodities(mockCommodity)).thenReturn(expectedCommodities);

        ResponseEntity<ArrayList<Commodity>> result = controllerUnderTest.getSuggestedCommodities(id);

        assertSame(expectedCommodities, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
}
