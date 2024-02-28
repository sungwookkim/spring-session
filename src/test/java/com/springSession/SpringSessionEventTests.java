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

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

@SpringBootTest
public class SpringSessionEventTests {
    private final static Logger logger = LoggerFactory.getLogger(SpringSessionEventTests.class);

    @Autowired FindByIndexNameSessionRepository mongoIndexedSessionRepository;

    @Test
    @DisplayName("스프링 세션 SessionCreatedEvent 이벤트 테스트")
    void sessionTest_1() {
        // given, when
        String sessionId = this.mongoIndexedSessionRepository.createSession().getId();

        // then
        Assertions.assertNotNull(sessionId);
    }

    @Test
    @DisplayName("스프링 세션 SessionDeletedEvent 이벤트 테스트")
    void sessionTest_2() {
        // given
        final String SESSION_NAME = "SESSION_NAME";
        final String SESSION_VALUE = "SESSION_VALUE";

        String sessionId = this.mongoIndexedSessionRepository.createSession().getId();

        TestMongoSession testMongoSession = new TestMongoSession(sessionId);
        testMongoSession.setAttribute(SESSION_NAME, SESSION_VALUE);

        this.mongoIndexedSessionRepository.save(testMongoSession.getMongoSession());

        // when
        this.mongoIndexedSessionRepository.deleteById(sessionId);
        Session session = this.mongoIndexedSessionRepository.findById(sessionId);

        // then
        Assertions.assertNull(session);
    }

    @Test
    @DisplayName("스프링 세션 SessionExpiredEvent 이벤트 테스트")
    void sessionTest_3() {
        // given
        final String SESSION_NAME = "SESSION_NAME";
        final String SESSION_VALUE = "SESSION_VALUE";

        String sessionId = this.mongoIndexedSessionRepository.createSession().getId();

        TestMongoSession testMongoSession = new TestMongoSession(sessionId);
        testMongoSession.setExpireAt(Date.from(LocalDateTime.now().minusMinutes(1L).atZone(ZoneId.systemDefault()).toInstant()));
        testMongoSession.setAttribute(SESSION_NAME, SESSION_VALUE);

        this.mongoIndexedSessionRepository.save(testMongoSession.getMongoSession());

        // when
        Session session = this.mongoIndexedSessionRepository.findById(sessionId);

        // then
        Assertions.assertNull(session);
    }
}
