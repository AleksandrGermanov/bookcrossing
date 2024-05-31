package ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.astondevs.trainee.bookcrossing.commons.RequestLogDto;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.dao.RequestLogDao;
import ru.astondevs.trainee.bookcrossing.loggingserver.requestlog.mapping.RequestLogMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {
    private final RequestLogDao requestLogDao;
    private final RequestLogMapper requestLogMapper;

    @Transactional
    @Override
    public void saveRequestLog(RequestLogDto requestLogDto) {
        requestLogDao.save(requestLogMapper.toModel(requestLogDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestLogDto> retrieveRequestLogList(Integer from, Integer size) {
        int offset = from / size;
        return requestLogDao.findAll(PageRequest.of(offset, size, Sort.by("createdOn").descending()))
                .stream()
                .map(requestLogMapper::toDto)
                .toList();
    }
}
