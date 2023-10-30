package controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
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

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private AuthenticationController controllerUnderTest;

    @Mock
    private Baloot mockBaloot;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        controllerUnderTest = new AuthenticationController();
        controllerUnderTest.setBaloot(mockBaloot);
    }

    @Test
    void shouldLoginSuccessfully() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "user1");
        input.put("password", "password");

        ResponseEntity<String> result = controllerUnderTest.login(input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("login successfully!", result.getBody());
        verify(mockBaloot).login("user1", "password");
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "user1");
        input.put("password", "password");
        doThrow(new NotExistentUser()).when(mockBaloot).login("user1", "password");

        ResponseEntity<String> result = controllerUnderTest.login(input);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("User does not exist.", result.getBody());
    }

    @Test
    void shouldReturnUnauthorizedWhenPasswordIsIncorrect() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "user1");
        input.put("password", "incorrectPassword");
        doThrow(new IncorrectPassword()).when(mockBaloot).login("user1", "incorrectPassword");

        ResponseEntity<String> result = controllerUnderTest.login(input);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Incorrect password.", result.getBody());
    }

    @Test
    void shouldSignupSuccessfully() throws UsernameAlreadyTaken {
        Map<String, String> input = new HashMap<>();
        input.put("address", "Address");
        input.put("birthDate", "2000-01-01");
        input.put("email", "user@example.com");
        input.put("username", "user1");
        input.put("password", "password");

        ResponseEntity<String> result = controllerUnderTest.signup(input);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("signup successfully!", result.getBody());
        verify(mockBaloot).addUser(any(User.class));
    }

    @Test
    void shouldReturnBadRequestWhenUsernameIsAlreadyTaken() throws UsernameAlreadyTaken {
        Map<String, String> input = new HashMap<>();
        input.put("address", "Address");
        input.put("birthDate", "2000-01-01");
        input.put("email", "user@example.com");
        input.put("username", "user1");
        input.put("password", "password");
        doThrow(new UsernameAlreadyTaken()).when(mockBaloot).addUser(any(User.class));

        ResponseEntity<String> result = controllerUnderTest.signup(input);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("The username is already taken.", result.getBody());
    }
}
