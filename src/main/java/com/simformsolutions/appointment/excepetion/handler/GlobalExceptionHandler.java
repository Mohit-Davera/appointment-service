package com.simformsolutions.appointment.excepetion.handler;

import com.simformsolutions.appointment.dto.ErrorResponse;
import com.simformsolutions.appointment.excepetion.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public Map<String, String> exception(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        Set<ConstraintViolation<?>> set = ex.getConstraintViolations();
        set.forEach(
                constraintViolation -> errors.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessageTemplate())
        );
        return errors;
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> exception(DataIntegrityViolationException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.CONFLICT.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = DoctorNotAvailableException.class)
    public ResponseEntity<Object> exception(DoctorNotAvailableException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AppointmentNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Appointment Found With This ID")
    public ResponseEntity<Object> exception(AppointmentNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> exception(UserNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SpecialistNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot Find Specialist For Your Issue")
    public ResponseEntity<Object> exception(SpecialistNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SpecialityException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Specialist Found For Your Issue")
    public ResponseEntity<Object> exception(SpecialityException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = DoctorNotFoundException.class)
    public ResponseEntity<Object> exception(DoctorNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = StatusChangeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot Change Status Of This Appointment")
    public ResponseEntity<Object> exception(StatusChangeException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ScheduleNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot Find Schedule With This Id")
    public ResponseEntity<Object> exception(ScheduleNotFoundException exception) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.name(), exception.getMessage(), LocalDateTime.now(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

}
