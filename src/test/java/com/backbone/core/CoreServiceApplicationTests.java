package com.backbone.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CoreServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        //todo: initial records vs initialize records here for testing?
        // consider this: delete all records, create new ones, then test
    }

    //  Read ops
    @Test
    public void productShouldReturnOneRecord() throws Exception {
        String url = "/product/1";
        String expectedMessage = "\"id\":1";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(content().string(containsString("reviews")))
                .andExpect(content().string(containsString(expectedMessage)));
    }

    @Test
    public void productWithNotAvailableIDAndReturnsNotFound() throws Exception {
        String url = "/product/200";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(404))
                .andExpect(content().string(blankOrNullString()));
    }

    @Test
    public void productWithNotValidIDAndReturnsNotFound() throws Exception {
        String url = "/product/1ax";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(content().string(blankOrNullString()));
    }

    @Test
    public void productWithMissingIDAndReturnNotSupportedMethod() throws Exception {
        String url = "/product/";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(405))
                .andExpect(content().string(blankOrNullString()));
    }

    //
    @Test
    public void productsWithZeroPagingOneHundredSizeAndReturnOneHundredRecords() throws Exception {
        String url = "/products/page/0/size/100";
        String expectedMessage = "\"id\":100";
        int recordCount = 100;

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$..id", hasSize(recordCount)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(content().string(containsString(expectedMessage)));
    }

    @Test
    public void productsWithZeroPagingOneSizeAndReturnsOneRecord() throws Exception {
        String url = "/products/page/0/size/1";
        String expectedMessage = "\"id\":1";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(expectedMessage)));
    }

    @Test
    public void productsSupportDefaultPaging() throws Exception {
        String url = "/products/";
        String expectedMessage = "\"id\":100";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(not(containsString(expectedMessage))));
    }

    @Test
    public void productsWithNotValidPageNumberAndReturnsNotFound() throws Exception {
        String url = "/products/page/1a";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(content().string(blankOrNullString()));

        //todo: Bad request is more appropriate, but it expects String in url. May be a ValidationService step?
    }

    @Test
    public void productsWithNotValidSizeNumberAndReturnsNotFound() throws Exception {
        String url = "/products/page/1/size/2a";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(content().string(blankOrNullString()));

        //todo: Bad request is more appropriate, but it expects String in url. May be a ValidationService step?
    }

    @Test
    public void productsWithBigPageNumberAndReturnsNoContent() throws Exception {
        String url = "/products/page/20/size/10";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(204))
                .andExpect(content().string(blankOrNullString()));
    }

    // service ops

    // save
    @Test
    public void productUpdateAllFieldsWithID() throws Exception {
        String url = "/product";
        String updatedProduct = "{\"id\":10," +
                                "\"category\":\"Book\"," +
                                "\"title\":\"title\"," +
                                "\"subTitle\":\"subtitle\"," +
                                "\"brand\":\"brand\"," +
                                "\"rating\":4," +
                                "\"shortDescription\":\"short description\"," +
                                "\"description\":\"description\"}";

        this.mockMvc.perform(post(url)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(updatedProduct))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.subTitle", is("subtitle")))
                .andExpect(jsonPath("$.brand", is("brand")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.shortDescription", is("short description")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    //todo: First, implement partial update w/ PUT, then test will pass
    @Test
    public void productUpdatePartiallyWithID() throws Exception {

        // set last values, then check
        String url = "/product";
        String updatedProduct = "{\"id\":10," +
                                "\"category\":\"Book\"," +
                                "\"title\":\"title\"," +
                                "\"subTitle\":\"subtitle\"," +
                                "\"brand\":\"brand\"," +
                                "\"rating\":4," +
                                "\"shortDescription\":\"short description\"," +
                                "\"description\":\"description\"}";

        this.mockMvc.perform(post(url)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(updatedProduct))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.subTitle", is("subtitle")))
                .andExpect(jsonPath("$.brand", is("brand")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.shortDescription", is("short description")))
                .andExpect(jsonPath("$.description", is("description")));

        //update partially
        String lastUpdatedReview = "{\"id\":10,\n" +
                                   " \"title\":\"title PARTIAL\",\n" +
                                   " }";

        this.mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(lastUpdatedReview))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("title PARTIAL")))
                .andExpect(jsonPath("$.subTitle", is("subtitle")))
                .andExpect(jsonPath("$.brand", is("brand")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.shortDescription", is("short description")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    public void productCreateWithoutID() throws Exception {
        String url = "/product";

        //todo: be sure >100 id is not available,
        // or delete all available records, first

        String newProduct = "{" +
                            "\"category\":\"Book\"," +
                            "\"title\":\"title\"," +
                            "\"subTitle\":\"subtitle\"," +
                            "\"brand\":\"brand\"," +
                            "\"rating\":4," +
                            "\"shortDescription\":\"short description\"," +
                            "\"description\":\"description\"}";

        this.mockMvc.perform(post(url)
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(newProduct))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(greaterThan(100))))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.subTitle", is("subtitle")))
                .andExpect(jsonPath("$.brand", is("brand")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.shortDescription", is("short description")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    @Test
    public void productCreateWithoutRequestBodyAndReturnsBadRequest() throws Exception {
        String url = "/product";

        this.mockMvc.perform(post(url)
                              .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void productCreateWithIDButReturnsAutoGeneratedID() throws Exception {
        String url = "/product";
        String newProduct = "{\"id\":1500," +
                            "\"category\":\"Book\"," +
                            "\"title\":\"title\"," +
                            "\"subTitle\":\"subtitle\"," +
                            "\"brand\":\"brand\"," +
                            "\"rating\":4," +
                            "\"shortDescription\":\"short description\"," +
                            "\"description\":\"description\"}";

        this.mockMvc.perform(post(url)
                               .contentType(MediaType.APPLICATION_JSON)
                               .content(newProduct))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id", is(not(1500))))
                .andExpect(jsonPath("$.title", is("title")))
                .andExpect(jsonPath("$.subTitle", is("subtitle")))
                .andExpect(jsonPath("$.brand", is("brand")))
                .andExpect(jsonPath("$.rating", is(4)))
                .andExpect(jsonPath("$.shortDescription", is("short description")))
                .andExpect(jsonPath("$.description", is("description")));
    }

    // delete
    @Test
    public void productDeleteWithID() throws Exception {
        String url = "/product/99";

        this.mockMvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        //check deleted record
        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is(404));
    }

    //todo: after some operations, there must be steps (or other kind of methods)
    // which checks the cache
}
