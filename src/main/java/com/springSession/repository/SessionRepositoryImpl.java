package com.springSession.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SessionRepositoryImpl<T> {
    private final List<T> memberRepository = new ArrayList<>();

    public List<T> getMemberRepository() {
        return memberRepository;
    }
}
