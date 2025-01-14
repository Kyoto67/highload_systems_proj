package ru.ifmo.cs.interviewers.presentation.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import ru.ifmo.cs.misc.UserId;

public record DemoteInterviewerRequestBodyDto(

        @JsonProperty("user_id")
        @Schema(description = "Unique identifier of the user to demote from interviewer role", example = "12345")
        UserId userId

) {}