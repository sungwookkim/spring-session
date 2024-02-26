package com.springSession.controller.composition.impl.session.v1;

import com.springSession.entity.CompositionMember;
import com.springSession.controller.composition.interfaces.session.command.v1.CompositionSessionCommandControllerV1;
import com.springSession.controller.composition.interfaces.session.read.v1.CompositionSessionReadControllerV1;
import com.springSession.entity.SessionInfo;
import com.springSession.repository.SessionRepositoryImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/composition/v1/session")
public class CompositionSessionControllerV1Impl implements CompositionSessionReadControllerV1
        , CompositionSessionCommandControllerV1 {

    private final static Logger logger = LoggerFactory.getLogger(CompositionSessionControllerV1Impl.class);

    private final SessionRepositoryImpl<CompositionMember> sessionRepositoryImpl;

    public CompositionSessionControllerV1Impl(SessionRepositoryImpl<CompositionMember> sessionRepositoryImpl) {
        this.sessionRepositoryImpl = sessionRepositoryImpl;
    }

    @Override
    public SessionInfo initSession(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        logger.info("init Session Id : {}", sessionId);

        return new SessionInfo(sessionId);
    }

    @Override
    public CompositionMember findSessionData() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        logger.info("findSessionData Session Id: {}", request.getSession().getId());

        return new CompositionMember(request.getSession().getAttribute("id").toString()
                , request.getSession().getAttribute("password").toString()
                , request.getSession().getAttribute("phoneNumber").toString());
    }

    @Override
    public boolean join(CompositionMember compositionMember) {
        this.sessionRepositoryImpl.getMemberRepository().add(compositionMember);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        logger.info("join Session Id: {}", request.getSession().getId());

        request.getSession().setAttribute("id", compositionMember.id());
        request.getSession().setAttribute("password", compositionMember.password());
        request.getSession().setAttribute("phoneNumber", compositionMember.phoneNumber());

        return true;
    }
}
