package com.springSession.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CompositionMember(@JsonProperty("id") String id
        , @JsonProperty("password") String password
        , @JsonProperty("phoneNumber") String phoneNumber) {

    public static CompositionMember emptyMember() {
        return new CompositionMember("", "", "");
    }
}
