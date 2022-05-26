package com.vire.globalexception;

import com.vire.exception.FileStorageException;
import com.vire.exception.VerifyEmailMobileNumberException;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleMultipartException(HttpServletRequest request) {
        return new ErrorInfo(request, "Invalid Upload Request");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorInfo handleMethodNotSupported(HttpServletRequest request) {
        return new ErrorInfo(request, "HTTP request method not supported for this operation.");
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorInfo handleIOException(HttpServletRequest request, Exception ex) {
        return new ErrorInfo(request, "IO Error: " + ex.getMessage());
    }
    @ExceptionHandler(FileStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorInfo handleFileUploadException(HttpServletRequest request, Exception ex){
        return new ErrorInfo(request, "FILE UPLOAD DOWNLOAD Error: " + ex.getMessage());
    }
    @ExceptionHandler(VerifyEmailMobileNumberException.class)
    @ResponseStatus(HttpStatus.FOUND)
    public ErrorInfo handleEmailMobileNumberException(HttpServletRequest request, Exception ex){
        return new ErrorInfo(request, "ALREADY EXISTS Error: " + ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorInfo handleException(HttpServletRequest request, Exception ex) {
        ex.printStackTrace();
        return new ErrorInfo(request, "Error: " + ex.getMessage());
    }
}