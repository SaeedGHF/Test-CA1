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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BalootApplication.class)
class CommoditiesControllerTest {

    private Baloot baloot;

    private Database database;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String EXIST_COMMODITY_ID = "1";
    public static final String NOT_EXIST_COMMODITY_ID = "0";
    public static final String EMPTY_LIST = "[]";
    public static final String NULL = "";
    public static final String VALID_RATE = "10";
    public static final String INVALID_RATE = "x10";
    public static final String EXIST_USERNAME = "ali";
    public static final String NOT_EXIST_USERNAME = "saeed";
    public static final String SAMPLE_COMMENT = "Such a wow!";
    public static final String VALID_SEARCH_OPTION = "name";
    public static final String INVALID_SEARCH_OPTION = "name2";
    public static final String EXIST_SEARCH_VALUE = "Galaxy";
    public static final String NOT_EXIST_SEARCH_VALUE = "Galaxy2";

    @BeforeEach
    public void setUp() {
        baloot = Baloot.getInstance();
        database = Database.getInstance();
    }

    @Test
    public void getCommoditiesTest_noError() throws Exception {
        mvc.perform(get("/commodities"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(database.getCommodities())));
    }

    @Test
    public void getCommodityTest_commodityFound() throws Exception {
        Commodity expectedCommodity = null;
        for (Commodity commodity : Database.getInstance().getCommodities())
            if (Objects.equals(commodity.getId(), EXIST_COMMODITY_ID))
                expectedCommodity = commodity;
        mvc.perform(get("/commodities/" + EXIST_COMMODITY_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expectedCommodity)));
    }

    @Test
    public void getCommodityTest_commodityNotFound() throws Exception {
        mvc.perform(get("/commodities/" + NOT_EXIST_COMMODITY_ID))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(NULL));
    }

    @Test
    public void getCommodityCommentTest_commentExists() throws Exception {
        ArrayList<Comment> comments = new ArrayList<>();
        for (Comment comment : Database.getInstance().getComments())
            if (comment.getCommodityId() == Integer.parseInt(EXIST_COMMODITY_ID))
                comments.add(comment);
        mvc.perform(get("/commodities/" + EXIST_COMMODITY_ID + "/comment"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(comments)));
    }

    @Test
    public void getCommodityCommentTest_commentNotExists() throws Exception {
        mvc.perform(get("/commodities/" + NOT_EXIST_COMMODITY_ID + "/comment"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(EMPTY_LIST));
    }

    @Test
    public void getSuggestedCommoditiesTest_suggestionExists() throws Exception {
        mvc.perform(get("/commodities/" + EXIST_COMMODITY_ID + "/suggested"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(baloot.suggestSimilarCommodities(baloot.getCommodityById(EXIST_COMMODITY_ID)))));
    }

    @Test
    public void getSuggestedCommoditiesTest_suggestionNotExists() throws Exception {
        mvc.perform(get("/commodities/" + NOT_EXIST_COMMODITY_ID + "/suggested"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(EMPTY_LIST));
    }

    @Test
    public void rateCommodityTest_noError() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("rate", VALID_RATE);
        payload.put("username", EXIST_USERNAME);
        mvc.perform(post("/commodities/" + EXIST_COMMODITY_ID + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("rate added successfully!"));
    }

    @Test
    public void rateCommodityTest_notExistentCommodityException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("rate", VALID_RATE);
        payload.put("username", EXIST_USERNAME);
        mvc.perform(post("/commodities/" + NOT_EXIST_COMMODITY_ID + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(Errors.NOT_EXISTENT_COMMODITY));
    }

    @Test
    public void rateCommodityTest_numberFormatException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("rate", INVALID_RATE);
        payload.put("username", EXIST_USERNAME);
        mvc.perform(post("/commodities/" + EXIST_COMMODITY_ID + "/rate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void addCommodityCommentTest_noError() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", EXIST_USERNAME);
        payload.put("comment", SAMPLE_COMMENT);
        mvc.perform(post("/commodities/" + EXIST_COMMODITY_ID + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("comment added successfully!"));
    }


    @Test
    public void addCommodityCommentTest_notExistentUserException() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("username", NOT_EXIST_USERNAME);
        payload.put("comment", SAMPLE_COMMENT);
        mvc.perform(post("/commodities/" + EXIST_COMMODITY_ID + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(Errors.NOT_EXISTENT_USER));
    }

    @Test
    public void searchCommoditiesTest_noError() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("searchOption", VALID_SEARCH_OPTION);
        payload.put("searchValue", EXIST_SEARCH_VALUE);
        mvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(baloot.filterCommoditiesByName(EXIST_SEARCH_VALUE))));
    }

    @Test
    public void searchCommoditiesTest_noCommodityFound() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("searchOption", VALID_SEARCH_OPTION);
        payload.put("searchValue", NOT_EXIST_SEARCH_VALUE);
        mvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(EMPTY_LIST));
    }

    @Test
    public void searchCommoditiesTest_invalidSearchOption() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("searchOption", INVALID_SEARCH_OPTION);
        payload.put("searchValue", VALID_SEARCH_OPTION);
        mvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(EMPTY_LIST));
    }
}
