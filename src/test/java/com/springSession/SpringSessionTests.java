package com.springSession;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.mongo.TestMongoSession;

import java.util.Set;

@SpringBootTest
public class SpringSessionTests {
    private static final Logger logger = LoggerFactory.getLogger(SpringSessionTests.class);

    @Autowired FindByIndexNameSessionRepository mongoIndexedSessionRepository;

    @Test
    @DisplayName("스프링 세션 생성 테스트")
    void sessionTest_1() {
        // given, when
        String sessionId = this.mongoIndexedSessionRepository.createSession().getId();
        logger.info("create spring session Id : {}", sessionId);

        // then
        Assertions.assertNotNull(sessionId);
    }

    @Test
    @DisplayName("스프링 세션 생성 후 세션 저장, 조회")
    void sessionTest_2() {
        // given
        final String SESSION_NAME = "SESSION_NAME";
        final String SESSION_VALUE = "SESSION_VALUE";

        String sessionId = this.mongoIndexedSessionRepository.createSession().getId();
        logger.info("create spring session Id : {}", sessionId);

        TestMongoSession testMongoSession = new TestMongoSession(sessionId);
        testMongoSession.setAttribute(SESSION_NAME, SESSION_VALUE);

        this.mongoIndexedSessionRepository.save(testMongoSession.getMongoSession());

        // when
        Session session = this.mongoIndexedSessionRepository.findById(sessionId);
        Set<String> findAttrNames = session.getAttributeNames();
        for(String attrName : findAttrNames) {
            logger.info("find session attr name {}, value : {}", attrName, session.getAttribute(attrName));
        }

        // then
        Assertions.assertEquals(session.getAttribute(SESSION_NAME), SESSION_VALUE);
    }
}
