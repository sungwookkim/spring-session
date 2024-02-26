package org.springframework.session.data.mongo;

import org.springframework.session.MapSession;
import org.springframework.session.Session;
import org.springframework.session.SessionIdGenerator;
import org.springframework.session.UuidSessionIdGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * <pre>
 *     {@link MongoSession} 클래스가 접근 제한이 걸려있어 테스트를 위해 생성한 테스트용 MongoSession 클래스
 * </pre>
 */
public class TestMongoSession implements Session {
    private final MongoSession mongoSession;

    public TestMongoSession(String sessionId) {
        this(sessionId, MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);
    }

    TestMongoSession() {
        this(MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);
    }

    TestMongoSession(long maxInactiveIntervalInSeconds) {
        this(UuidSessionIdGenerator.getInstance().generate(), maxInactiveIntervalInSeconds);
    }

    TestMongoSession(SessionIdGenerator sessionIdGenerator) {
        this(sessionIdGenerator.generate(), MapSession.DEFAULT_MAX_INACTIVE_INTERVAL_SECONDS);
    }

    TestMongoSession(SessionIdGenerator sessionIdGenerator, long maxInactiveIntervalInSeconds) {
        this(sessionIdGenerator.generate(), maxInactiveIntervalInSeconds);
    }

    TestMongoSession(String id, long maxInactiveIntervalInSeconds) {
        this.mongoSession = new MongoSession(id, maxInactiveIntervalInSeconds);
    }

    @Override
    public String getId() {
        return this.mongoSession.getId();
    }

    @Override
    public String changeSessionId() {
        return this.mongoSession.changeSessionId();
    }

    @Override
    public <T> T getAttribute(String attributeName) {
        return this.mongoSession.getAttribute(attributeName);
    }

    @Override
    public <T> T getRequiredAttribute(String name) {
        return this.mongoSession.getRequiredAttribute(name);
    }

    @Override
    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        return this.mongoSession.getAttributeOrDefault(name, defaultValue);
    }

    @Override
    public Set<String> getAttributeNames() {
        return this.mongoSession.getAttributeNames();
    }

    @Override
    public void setAttribute(String attributeName, Object attributeValue) {
        this.mongoSession.setAttribute(attributeName, attributeValue);
    }

    @Override
    public void removeAttribute(String attributeName) {
        this.mongoSession.removeAttribute(attributeName);
    }

    @Override
    public Instant getCreationTime() {
        return this.mongoSession.getCreationTime();
    }

    @Override
    public void setLastAccessedTime(Instant lastAccessedTime) {
        this.mongoSession.setLastAccessedTime(lastAccessedTime);
    }

    @Override
    public Instant getLastAccessedTime() {
        return this.mongoSession.getLastAccessedTime();
    }

    @Override
    public void setMaxInactiveInterval(Duration interval) {
        this.mongoSession.setMaxInactiveInterval(interval);
    }

    @Override
    public Duration getMaxInactiveInterval() {
        return this.mongoSession.getMaxInactiveInterval();
    }

    @Override
    public boolean isExpired() {
        return this.mongoSession.isExpired();
    }

    public MongoSession getMongoSession() {
        return mongoSession;
    }
}
