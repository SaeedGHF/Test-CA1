package controllers;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import defines.Errors;
import model.Comment;
import model.Commodity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BalootApplication.class)
class UserControllerTest {

    private Baloot baloot;

    private Database database;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String EXIST_USERNAME = "ali";
    public static final String NOT_EXIST_USERNAME = "amir";
    public static final String NULL = "";
    public static final String VALID_CREDIT = "10000";
    public static final String INVALID_RANGE_CREDIT = "-10000";
    public static final String INVALID_CREDIT = "x1000";

    @BeforeEach
    public void setUp() {
        baloot = Baloot.getInstance();
        database = Database.getInstance();
    }

    @Test
    public void getUserTest_noError() throws Exception {
        mvc.perform(get("/users/" + EXIST_USERNAME))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(baloot.getUserById(EXIST_USERNAME))));
    }

    @Test
    public void getUserTest_notExistentUserException() throws Exception {
        mvc.perform(get("/users/" + NOT_EXIST_USERNAME))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(NULL));
    }

    @Test
    public void addCreditTest_noError() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("credit", VALID_CREDIT);
        mvc.perform(post("/users/" + EXIST_USERNAME + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("credit added successfully!"));
    }

    @Test
    public void addCreditTest_invalidCreditRangeException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("credit", INVALID_RANGE_CREDIT);
        mvc.perform(post("/users/" + EXIST_USERNAME + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Errors.INVALID_CREDIT_RANGE));
    }

    @Test
    public void addCreditTest_numberFormatException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("credit", INVALID_CREDIT);
        mvc.perform(post("/users/" + EXIST_USERNAME + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }


    @Test
    public void addCreditTest_notExistentUserException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("credit", VALID_CREDIT);
        mvc.perform(post("/users/" + NOT_EXIST_USERNAME + "/credit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(Errors.NOT_EXISTENT_USER));
    }


}
