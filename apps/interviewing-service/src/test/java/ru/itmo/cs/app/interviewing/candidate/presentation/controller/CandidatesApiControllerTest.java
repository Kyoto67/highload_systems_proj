package ru.itmo.cs.app.interviewing.candidate.presentation.controller;

import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.ifmo.cs.domain_event.domain.stored_event.StoredDomainEventRepository;
import ru.ifmo.cs.misc.Name;
import ru.ifmo.cs.service_token.application.ServiceTokenResolver;
import ru.ifmo.cs.service_token.domain.RequestData;
import ru.ifmo.cs.service_token.domain.ServiceId;
import ru.itmo.cs.app.interviewing.AbstractIntegrationTest;
import ru.itmo.cs.app.interviewing.candidate.application.command.AddCandidateCommand;
import ru.itmo.cs.app.interviewing.candidate.domain.Candidate;
import ru.itmo.cs.app.interviewing.candidate.domain.CandidateRepository;
import ru.itmo.cs.app.interviewing.candidate.domain.value.CandidateId;
import ru.itmo.cs.app.interviewing.candidate.presentation.controller.dto.request.AddCandidateRequestBodyDto;
import ru.itmo.cs.command_bus.CommandBus;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("web")
class CandidatesApiControllerTest extends AbstractIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    CommandBus commandBus;
    @Autowired
    ServiceTokenResolver serviceTokenResolver;

    @Test
    void testSuccessfullyAddingNewCandidate() throws Exception {
        Assertions.assertTrue(candidateRepository.findAll().isEmpty());
        AddCandidateRequestBodyDto requestBody = new AddCandidateRequestBodyDto("John Doe");

        mockMvc.perform(post("/api/v1/candidates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody))
                        .header("X-Service-Token",
                                serviceTokenResolver.resolveServiceTokenFor(new RequestData(new ServiceId(1),
                                        new ServiceId(6))).value()))
                .andExpect(status().isOk());
        Assertions.assertEquals(1, candidateRepository.findAll().size());
    }

    @Test
    void testAttemptingToAddCandidateWithEmptyName() throws Exception {
        int totalCount = candidateRepository.findAll().size();

        AddCandidateRequestBodyDto requestBody = new AddCandidateRequestBodyDto("");

        mockMvc.perform(post("/api/v1/candidates/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody))
                        .header("X-Service-Token",
                                serviceTokenResolver.resolveServiceTokenFor(new RequestData(new ServiceId(1),
                                        new ServiceId(6))).value()))
                .andExpect(status().isBadRequest());
        Assertions.assertTrue(candidateRepository.findAll().size() == totalCount);
    }

    @Test
    void testRetrievingCandidateByValidId() throws Exception {
        commandBus.submit(new AddCandidateCommand(Name.of("my name")));
        Candidate createdCandidate = candidateRepository.findAll().stream().findAny().orElseThrow();

        mockMvc.perform(get("/api/v1/candidates/by-id")
                        .param("candidateId", createdCandidate.getId().value().toString())
                        .header("X-Service-Token",
                                serviceTokenResolver.resolveServiceTokenFor(new RequestData(new ServiceId(1),
                                        new ServiceId(6))).value()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.candidate_id").value(createdCandidate.getId().value().toString()))
                .andExpect(jsonPath("$.status").value(createdCandidate.getStatus().value()))
                .andExpect(jsonPath("$.candidate_name.fullName").value(createdCandidate.getName().fullName()));
    }

    @Test
    void testRetrievingCandidateByInvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/candidates/by-id")
                        .param("candidateId", CandidateId.generate().value().toString())
                        .header("X-Service-Token",
                                serviceTokenResolver.resolveServiceTokenFor(new RequestData(new ServiceId(1),
                                        new ServiceId(6))).value()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRetrievingAllCandidatesWithPagination() throws Exception {
        int totalCount = candidateRepository.findAll().size();
        int page = 0;
        int size = 50;
        Stream.of(Name.of("first name"),
                        Name.of("second name"),
                        Name.of("third name")).map(AddCandidateCommand::new)
                .forEach(command -> commandBus.submit(command));

        mockMvc.perform(get("/api/v1/candidates")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .header("X-Service-Token",
                                serviceTokenResolver.resolveServiceTokenFor(new RequestData(new ServiceId(1),
                                        new ServiceId(6))).value()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", String.valueOf(totalCount + 3)));
    }

}
