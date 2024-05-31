package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.model.RequestLog;

@Repository
public interface RequestLogDao extends JpaRepository<RequestLog, Long> {
}
