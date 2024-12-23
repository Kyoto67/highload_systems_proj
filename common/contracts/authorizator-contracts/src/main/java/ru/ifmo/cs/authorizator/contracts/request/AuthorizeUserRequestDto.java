package ru.ifmo.cs.authorizator.contracts.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizeUserRequestDto(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password
) {}
