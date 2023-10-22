package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, "user@mail.com", "username", 1, "text");
    }

    @Test
    public void testAddUserVote() {
        comment.addUserVote("user1", "like");
        assertEquals(1, comment.getLike());
        assertEquals(0, comment.getDislike());

        comment.addUserVote("user2", "dislike");
        assertEquals(1, comment.getLike());
        assertEquals(1, comment.getDislike());
    }

    @Test
    public void testGetCurrentDate() {
        String date = comment.getCurrentDate();
        assertNotNull(date);
    }
}
