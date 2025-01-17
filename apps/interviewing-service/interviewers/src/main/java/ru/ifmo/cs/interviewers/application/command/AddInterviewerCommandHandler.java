package ru.ifmo.cs.interviewers.application.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ifmo.cs.misc.Name;
import ru.ifmo.cs.passport.api.PassportFeignClient;
import ru.ifmo.cs.interviewers.application.command.AddInterviewerCommand;
import ru.ifmo.cs.interviewers.domain.Interviewer;
import ru.ifmo.cs.interviewers.domain.InterviewerRepository;
import ru.itmo.cs.command_bus.CommandHandler;

@Service
@AllArgsConstructor
public class AddInterviewerCommandHandler implements CommandHandler<AddInterviewerCommand> {
    private final InterviewerRepository repository;
    private final PassportFeignClient passportClient;
    @Override
    public void handle(AddInterviewerCommand command) {
        Name nameByUserId = passportClient.getUser(command.userId.getUid()).name();
        Interviewer interviewer = Interviewer.create(command.userId, nameByUserId);
        repository.save(interviewer);
    }
}
