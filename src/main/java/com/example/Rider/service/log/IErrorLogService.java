package com.example.Rider.service.log;

public interface IErrorLogService {
    void saveErrorLog(String errorFor, String message);

    void saveErrorLog(String errorFor, String message, Long tranId);
}
