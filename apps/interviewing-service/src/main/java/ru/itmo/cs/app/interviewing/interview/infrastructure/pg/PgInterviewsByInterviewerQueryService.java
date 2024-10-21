package ru.itmo.cs.app.interviewing.interview.infrastructure.pg;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Service;
import ru.itmo.cs.app.interviewing.interview.application.query.InterviewsByInterviewerQueryService;
import ru.itmo.cs.app.interviewing.interview.application.query.dto.InterviewsByInterviewerDto;
import ru.itmo.cs.app.interviewing.interview.domain.Interview;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.entity.PgInterviewEntity;
import ru.itmo.cs.app.interviewing.interview.infrastructure.pg.mapper.PgInterviewEntityRowMapper;
import ru.itmo.cs.app.interviewing.interviewer.domain.value.InterviewerId;

@Primary
@Service
@AllArgsConstructor
public class PgInterviewsByInterviewerQueryService implements InterviewsByInterviewerQueryService {
    private final NamedParameterJdbcOperations jdbcOperations;
    private final PgInterviewEntityRowMapper pgInterviewEntityRowMapper;
    private final PgScheduleEntityDao pgScheduleEntityDao;

    private final static String QUERY = """
            select *
            from interviews
            where interviewer_id = :interviewerId
            order by created_at
            """;

    @Override
    public InterviewsByInterviewerDto findBy(InterviewerId interviewerId) {
        List<PgInterviewEntity> interviewsByInterviewer = jdbcOperations.query(
                QUERY,
                new MapSqlParameterSource().addValue("interviewerId", interviewerId.value()),
                pgInterviewEntityRowMapper
        );

        return new InterviewsByInterviewerDto(
                interviewerId,
                interviewsByInterviewer.stream().map(e -> Interview.hydrate(e, pgScheduleEntityDao.findFor(e))).toList()
        );
    }

}
