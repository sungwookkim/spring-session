package com.springSession.controller.composition.interfaces.session.read.v1;

import com.springSession.entity.CompositionMember;
import com.springSession.entity.SessionInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

public interface CompositionSessionReadControllerV1 {

    @GetMapping("/init")
    SessionInfo initSession(HttpServletRequest request);

    @GetMapping()
    CompositionMember findSessionData();
}
