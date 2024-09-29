package ru.itmo.cs.app.interviewing.interviewer.application.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ifmo.cs.misc.Name;
import ru.ifmo.cs.passport.api.PassportClient;
import ru.itmo.cs.app.interviewing.interviewer.domain.Interviewer;
import ru.itmo.cs.app.interviewing.interviewer.domain.InterviewerRepository;
import ru.itmo.cs.command_bus.CommandHandler;

@Service
@AllArgsConstructor
public class AddInterviewerCommandHandler implements CommandHandler<AddInterviewerCommand> {
    private final InterviewerRepository repository;
    private final PassportClient passportClient;
    @Override
    public void handle(AddInterviewerCommand command) {
        Name nameByUserId = passportClient.getNameByUserId(command.userId);
        Interviewer interviewer = Interviewer.create(command.userId, nameByUserId);
        repository.save(interviewer);
    }
}
