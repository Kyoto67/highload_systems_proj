package ru.itmo.cs.app.interviewing.interview.presentation.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;
import ru.itmo.cs.app.interviewing.interview.application.command.CancelInterviewCommand;
import ru.itmo.cs.app.interviewing.interview.application.command.RescheduleInterviewCommand;
import ru.itmo.cs.app.interviewing.interview.application.command.ScheduleInterviewCommand;
import ru.itmo.cs.app.interviewing.interview.application.query.InterviewPageQueryService;
import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewPage;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.domain.InterviewRepository;
import ru.itmo.cs.app.interviewing.interview.domain.value.InterviewId;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.request.CancelInterviewRequestBodyDto;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.request.RescheduleInterviewRequestBodyDto;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.request.ScheduleInterviewRequestBodyDto;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.response.GetAllInterviewsResponseBodyDto;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.response.GetInterviewResponseBodyDto;
import ru.itmo.cs.app.interviewing.interview.presentation.controller.dto.response.InterviewResponseDto;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;
import ru.itmo.cs.command_bus.CommandBus;

@RestController
public class InterviewApiController {
    private final CommandBus commandBus;
    private final InterviewRepository interviewRepository;
    private final InterviewPageQueryService interviewPageQueryService;

    public InterviewApiController(
            CommandBus commandBus,
            @Qualifier("pgInterviewRepository") InterviewRepository interviewRepository,
            @Qualifier("pgInterviewPageQueryService") InterviewPageQueryService interviewPageQueryService
    ) {
        this.commandBus = commandBus;
        this.interviewRepository = interviewRepository;
        this.interviewPageQueryService = interviewPageQueryService;
    }

    @PostMapping("/api/v1/interviews/schedule")
    public ResponseEntity<?> scheduleInterview(
            @RequestBody ScheduleInterviewRequestBodyDto scheduleInterviewRequestBodyDto
    ) {
        commandBus.submit(new ScheduleInterviewCommand(
                InterviewerId.hydrate(scheduleInterviewRequestBodyDto.interviewerId()),
                CandidateId.hydrate(scheduleInterviewRequestBodyDto.candidateId()),
                scheduleInterviewRequestBodyDto.scheduledTime()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/interviews/reschedule")
    public ResponseEntity<?> rescheduleInterview(
            @RequestBody RescheduleInterviewRequestBodyDto rescheduleInterviewRequestBodyDto
    ) {
        commandBus.submit(new RescheduleInterviewCommand(
                InterviewId.hydrate(rescheduleInterviewRequestBodyDto.interviewId()),
                rescheduleInterviewRequestBodyDto.newScheduledTime()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/interviews/cancel")
    public ResponseEntity<?> cancelInterview(
            @RequestBody CancelInterviewRequestBodyDto cancelInterviewRequestBodyDto
    ) {
        commandBus.submit(new CancelInterviewCommand(InterviewId.hydrate(cancelInterviewRequestBodyDto.interviewId())));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/v1/interviews/by-id")
    public ResponseEntity<?> getInterviewById(@RequestParam String interviewId) {
        Interview interview = interviewRepository.findById(InterviewId.hydrate(interviewId));
        return ResponseEntity.ok(new GetInterviewResponseBodyDto(InterviewResponseDto.from(interview)));
    }

    @GetMapping("/api/v1/interviews")
    public ResponseEntity<?> getAllInterviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            HttpServletResponse response
    ) {
        InterviewPage interviewPage = interviewPageQueryService.findFor(page, size);
        response.setHeader("X-Total-Count", String.valueOf(interviewPage.totalElements()));
        return ResponseEntity.ok(
                new GetAllInterviewsResponseBodyDto(interviewPage.content()
                                                                 .stream()
                                                                 .map(InterviewResponseDto::from)
                                                                 .toList()));
    }
}
