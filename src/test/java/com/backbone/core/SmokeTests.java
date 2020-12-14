package com.backbone.core;

import com.backbone.core.demo.DummyController;
import com.backbone.core.demo.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SmokeTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    DummyController dummyController;

    @Autowired
    ProductController productController;

    @Test
    void contextLoads() {
        assertThat(dummyController).isNotNull();
        assertThat(productController).isNotNull();
    }

    @Test
    public void dummyReturnsDefaultMessage() throws Exception {
        String url = "/dummy";
        String expectedMessage = "Hello";

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(expectedMessage)));
    }

    @Test
    public void dummyReturnGivenMessage() throws Exception {
        String messageGiven = "Abidin";
        String url = "/dummy/" + messageGiven;

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString(messageGiven)));
    }
}
