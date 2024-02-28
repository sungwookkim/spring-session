package com.springSession;

import com.springSession.entity.CompositionMember;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SpringSessionRestTests {
    private final static Logger logger = LoggerFactory.getLogger(SpringSessionRestTests.class);

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("[스프링 세션] 세션 생성 및 세션 ID 조회")
    void springSessionRest_1() throws Exception {
        // given, when
        MockHttpServletResponse response = mockMvc.perform(get("/composition/v1/session/init"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        String springSessionHeaderToken = response.getHeader("X-Auth-Token");
        /** Base64 Decode는 {@link org.springframework.session.web.http.DefaultCookieSerializer#base64Decode(String)} 참고. */
        String springSessionCookie = new String(Base64.getDecoder().decode(response.getCookie("SESSION").getValue()));

        logger.info("header token : {}, cookie : {}", springSessionHeaderToken, springSessionCookie);

        // then
        Assertions.assertNotNull(springSessionHeaderToken, springSessionCookie);
        Assertions.assertEquals(springSessionCookie, springSessionHeaderToken);
    }

    @Test
    @DisplayName("[스프링 세션] 세션 데이터 저장")
    void springSessionRest_2() throws Exception {
        // given, when, then
        CompositionMember compositionMember = new CompositionMember("sinnake", "password!", "01012341234");
        mockMvc.perform(post("/composition/v1/session")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(compositionMember)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    @DisplayName("[스프링 세션] 헤더 세션 ID를 이용해 세션 데이터 저장 후 조회")
    void springSessionRest_3() throws Exception {
        // given
        CompositionMember compositionMember = new CompositionMember("sinnake", "password!", "01012341234");
        MockHttpServletResponse response = mockMvc.perform(post("/composition/v1/session")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(compositionMember)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn()
                .getResponse();

        String springSessionHeaderToken = response.getHeader("X-Auth-Token");

        // when
        response = mockMvc.perform(get("/composition/v1/session")
                        .header("X-Auth-Token", springSessionHeaderToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        CompositionMember respCompositionMember = this.objectMapper.readValue(response.getContentAsString(), CompositionMember.class);
        logger.info("response : {}", respCompositionMember);

        // then
        Assertions.assertTrue(compositionMember.id().equals(respCompositionMember.id())
                && compositionMember.password().equals(respCompositionMember.password())
                && compositionMember.phoneNumber().equals(respCompositionMember.phoneNumber()));
    }

    @Test
    @DisplayName("[스프링 세션] 쿠키 세션 ID를 이용해 세션 데이터 저장 후 조회")
    void springSessionRest_4() throws Exception {
        // given
        CompositionMember compositionMember = new CompositionMember("sinnake", "password!", "01012341234");
        MockHttpServletResponse response = mockMvc.perform(post("/composition/v1/session")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(compositionMember)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andReturn()
                .getResponse();

        // when
        response = mockMvc.perform(get("/composition/v1/session")
                        .cookie(response.getCookies()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        CompositionMember respCompositionMember = this.objectMapper.readValue(response.getContentAsString(), CompositionMember.class);
        logger.info("response : {}", respCompositionMember);

        // then
        Assertions.assertTrue(compositionMember.id().equals(respCompositionMember.id())
                && compositionMember.password().equals(respCompositionMember.password())
                && compositionMember.phoneNumber().equals(respCompositionMember.phoneNumber()));
    }
}
