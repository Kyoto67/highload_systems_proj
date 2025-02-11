package ru.ifmo.cs.feedbacks.application.command;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.ifmo.cs.feedbacks.domain.value.FeedbackId;
import ru.itmo.cs.command_bus.Command;

@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public class SubmitFeedbackCommand implements Command {
    FeedbackId feedbackId;
}
