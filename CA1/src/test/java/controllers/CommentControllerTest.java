package controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import exceptions.NotExistentComment;
import model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private CommentController controllerUnderTest;

    @Mock
    private Baloot mockBaloot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        controllerUnderTest = new CommentController();
        controllerUnderTest.setBaloot(mockBaloot);
    }

    @Test
    void shouldLikeComment() throws NotExistentComment {
        int commentId = 1;
        String username = "user1";
        Map<String, String> input = Map.of("username", username);
        Comment mockComment = mock(Comment.class);
        when(mockBaloot.getCommentById(commentId)).thenReturn(mockComment);

        ResponseEntity<String> result = controllerUnderTest.likeComment(Integer.toString(commentId), input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("The comment was successfully liked!", result.getBody());
        verify(mockComment).addUserVote(username, "like");
    }

    @Test
    void shouldReturnNotFoundWhenLikingNonExistentComment() throws NotExistentComment {
        int commentId = 2;
        Map<String, String> input = Map.of("username", "user1");
        when(mockBaloot.getCommentById(commentId)).thenThrow(new NotExistentComment());

        ResponseEntity<String> result = controllerUnderTest.likeComment(Integer.toString(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Comment does not exist.", result.getBody());
    }

    @Test
    void shouldDislikeComment() throws NotExistentComment {
        int commentId = 1;
        String username = "user1";
        Map<String, String> input = Map.of("username", username);
        Comment mockComment = mock(Comment.class);
        when(mockBaloot.getCommentById(commentId)).thenReturn(mockComment);

        ResponseEntity<String> result = controllerUnderTest.dislikeComment(Integer.toString(commentId), input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("The comment was successfully disliked!", result.getBody());
        verify(mockComment).addUserVote(username, "dislike");
    }

    @Test
    void shouldReturnNotFoundWhenDislikingNonExistentComment() throws NotExistentComment {
        int commentId = 2;
        Map<String, String> input = Map.of("username", "user1");
        when(mockBaloot.getCommentById(commentId)).thenThrow(new NotExistentComment());

        ResponseEntity<String> result = controllerUnderTest.dislikeComment(Integer.toString(commentId), input);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Comment does not exist.", result.getBody());
    }
}
