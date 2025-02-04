package com.example.Rider.service.log;

import com.example.Rider.model.log.ErrorLog;
import com.example.Rider.model.log.repository.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorLogService implements IErrorLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorLogService.class);

    @Autowired
    private ErrorLogRepository errorLogRepository;

    @Override
    public void saveErrorLog(String errorFor, String message) {
        try {
            ErrorLog log = new ErrorLog();
            log.setErrorFor(errorFor);
            log.setMessage(message);

            errorLogRepository.save(log);
        } catch (Exception ex) {
            LOGGER.error("Save error log: " + ", Message: " + ex.toString());
        }
    }

    @Override
    public void saveErrorLog(String errorFor, String message, Long tranId) {
        try {
            ErrorLog log = new ErrorLog();
            log.setErrorFor(errorFor);
            log.setMessage(message);
            log.setTranId(tranId);

            errorLogRepository.save(log);
        } catch (Exception ex) {
            LOGGER.error("Save error log: " + ", Message: " + ex.toString());
        }
    }
}
