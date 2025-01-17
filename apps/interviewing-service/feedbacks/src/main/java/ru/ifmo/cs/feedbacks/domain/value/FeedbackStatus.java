package ru.ifmo.cs.feedbacks.domain.value;

import com.fasterxml.jackson.annotation.JsonValue;
import ru.ifmo.cs.string_enum.StringEnum;
import ru.ifmo.cs.string_enum.StringEnumResolver;

public enum FeedbackStatus implements StringEnum {
    WAITING_FOR_SUBMIT("waiting_for_submit"),
    SUBMITTED("submitted"),
    ;

    public static final StringEnumResolver<FeedbackStatus> R = StringEnumResolver.r(FeedbackStatus.class);

    @JsonValue private final String value;

    FeedbackStatus(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
