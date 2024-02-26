package com.springSession.controller.composition.interfaces.session.command.v1;

import com.springSession.entity.CompositionMember;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface CompositionSessionCommandControllerV1 {

    @PostMapping
    boolean join(@RequestBody CompositionMember compositionMember);
}
