package ru.ifmo.cs.authorizator.contracts.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizedUserTokenResponseBodyDto(
        @JsonProperty("token") String token
) {}
