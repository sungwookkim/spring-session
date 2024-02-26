package com.springSession.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.session.config.SessionRepositoryCustomizer;
import org.springframework.session.config.annotation.web.http.SpringHttpSessionConfiguration;
import org.springframework.session.data.mongo.MongoIndexedSessionRepository;
import org.springframework.session.data.mongo.config.annotation.web.http.MongoHttpSessionConfiguration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 *     Spring Session(MongoDB) 설정
 *
 *     Spring Session은 서버마다 세션을 관리하는게 아닌 하나의 세션 저장소를 두고 모든 서버가 세션 저장소를 사용함으로서 세션을 제어한다.
 *
 *     하나의 세션 저장소를 사용하므로서
 *     분산 웹 애플리케이션: 웹 애플리케이션이 여러 서버에 분산되어 있는 경우 사용자 세션을 관리하는 것이 어려울 수 있습니다. Spring Session은 세션 데이터를 공유 데이터베이스나 Redis에 저장하여 모든 서버가 세션 데이터에 액세스하고 업데이트할 수 있도록 함으로써 도움을 줄 수 있습니다.
 *     세션 확장성: 동시 사용자가 많은 대규모 웹 애플리케이션의 경우 서버의 메모리에 세션을 저장하면 확장성 문제가 발생할 수 있습니다. Spring 세션을 사용하면 세션 데이터를 영구 저장소에 저장하여 확장성을 개선하고 메모리 부족 오류의 위험을 줄일 수 있습니다.
 *     세션 백업 및 복구: 세션 데이터를 영구 저장소에 저장하면 서버 장애 또는 다운타임 발생 시 세션 데이터를 백업 및 복구할 수 있는 메커니즘을 제공할 수도 있습니다.
 *     와 같은 장점이 있음.
 *
 *     Spring Session의 기본으로 제공하는 세션 ID 제어 방법은 쿠키, 헤더 2가지가 있다.
 *     Spring Session에 필수로 사용 되는 클래스들은
 *     {@link org.springframework.session.FindByIndexNameSessionRepository} 세션 정보를 저장하는데 사용되는 Repository 인터페이스
 *     Spring Session MongoDB는 {@link MongoIndexedSessionRepository} 구현체 사용
 *
 *     {@link SpringHttpSessionConfiguration} Spring Session Config 구현체
 *     Spring Session MongoDB는 {@link MongoHttpSessionConfiguration} 구현체 사용
 *
 *     {@link org.springframework.session.web.http.SessionRepositoryFilter} Spring Session 필터
 *     등이 있다.
 * </pre>
 */
@Configuration
public class SpringSessionConfig {

    /**
     * <pre>
     *     세션 ID 제어 Resolver
     *     {@link HttpSessionIdResolver}는 세션 ID 제어 관련 인터페이스
     *     Spring Session에서 기본적으로 쿠키, 헤더 제어 구현체를 제공한다.
     * </pre>
     *
     * @return 기본적으론 쿠키 혹은 헤더 중 하나를 사용해야 하는데 두 군데 다 사용하기 위해 {@link HybridHttpSessionIdResolver} 구현체 반환. 해당 구현체는 자체적으로 만든 클래스.
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new HybridHttpSessionIdResolver();
    }

    /**
     * <pre>
     *     {@link MongoHttpSessionConfiguration#mongoSessionRepository(MongoOperations)}
     *     에서 반환하는 {@link MongoIndexedSessionRepository} 객체를 커스텀 하기 위한 Bean
     * </pre>
     */
    @Bean
    SessionRepositoryCustomizer<MongoIndexedSessionRepository> mongoSessionRepositoryCustomizer() {
        return (sessionRepository) -> {
            sessionRepository.setMaxInactiveIntervalInSeconds(MongoIndexedSessionRepository.DEFAULT_INACTIVE_INTERVAL);
        };
    }

    /**
     * <pre>
     *     {@link HttpSessionIdResolver} 구현체로 세션 ID를 쿠키/헤더 두 군데에 관리하기 위한 구현체.
     * </pre>
     */
    class HybridHttpSessionIdResolver implements HttpSessionIdResolver {
        private final Logger logger = LoggerFactory.getLogger(HybridHttpSessionIdResolver.class);

        private final HttpSessionIdResolver headerHttpSessionIdResolver = HeaderHttpSessionIdResolver.xAuthToken();
        private final HttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();

        @Override
        public List<String> resolveSessionIds(HttpServletRequest request) {
            List<String> headerSessionId = this.headerHttpSessionIdResolver.resolveSessionIds(request);
            if(!headerSessionId.isEmpty()) {
                return headerSessionId;
            }

            return this.cookieHttpSessionIdResolver.resolveSessionIds(request);
        }

        @Override
        public void setSessionId(HttpServletRequest request, HttpServletResponse response, String sessionId) {
            logger.info("session Id : {}", sessionId);
            this.headerHttpSessionIdResolver.setSessionId(request, response, sessionId);
            this.cookieHttpSessionIdResolver.setSessionId(request, response, sessionId);
        }

        @Override
        public void expireSession(HttpServletRequest request, HttpServletResponse response) {
            this.headerHttpSessionIdResolver.expireSession(request, response);
            this.cookieHttpSessionIdResolver.expireSession(request, response);
        }
    }

    /**
     * <pre>
     *     Spring Session의 세션 정보를 MongoDB에 저장하기 위한 접속 정보 클래스
     * </pre>
     */
    @Configuration
    static class SessionMongoDBConfig {
        private final String host;
        private final int port;
        private final String userName;
        private final String password;
        private final String authDatabase;

        public SessionMongoDBConfig(@Value("${spring.mongodb.host}")String host
                , @Value("${spring.mongodb.port}")int port
                , @Value("${spring.mongodb.username}")String userName
                , @Value("${spring.mongodb.password}")String password
                , @Value("${spring.mongodb.auth-database}")String authDatabase) {

            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
            this.authDatabase = authDatabase;
        }

        @Bean
        public MongoClient sessionMongoClient() {

            MongoCredential credential = MongoCredential.createCredential(this.userName
                    , this.authDatabase
                    , this.password.toCharArray());

            MongoClientSettings settings = MongoClientSettings.builder().credential(credential)
                    .applyToClusterSettings(builder -> builder
                            .hosts(Arrays.asList(new ServerAddress(this.host, this.port))))
                    .build();

            return MongoClients.create(settings);
        }

        @Bean
        public MongoOperations sessionMongoTemplate(MongoClient mongoClient) {

            MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "Session");
            ((MappingMongoConverter) mongoTemplate.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));

            return mongoTemplate;
        }
    }

    /**
     * <pre>
     *     Spring Session MongoDB에서 세션 저장소를 지정한 특정 MongoDB를 사용하기 위한 클래스
     * </pre>
     */
    @Configuration
    static class CustomMongoHttpSessionConfig extends MongoHttpSessionConfiguration {

        @Override
        public MongoIndexedSessionRepository mongoSessionRepository(MongoOperations sessionMongoTemplate) {
            return super.mongoSessionRepository(sessionMongoTemplate);
        }
    }
}
