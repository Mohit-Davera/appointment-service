package com.simformsolutions.appointment.excepetion.handler;

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
    @ResponseStatus(code = HttpStatus.CONFLICT, reason = "Email Already Exists")
    public ResponseEntity<Object> exception(DataIntegrityViolationException exception) {
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = NoDoctorAvailableExcepetion.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Doctor Available For This Date")
    public ResponseEntity<Object> exception(NoDoctorAvailableExcepetion ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoAppointmentFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Appointment Found With This ID")
    public ResponseEntity<Object> exception(NoAppointmentFoundException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No User Found With This ID")
    public ResponseEntity<Object> exception(UserNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSpecialistFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot Find Specialist For Your Issue")
    public ResponseEntity<Object> exception(NoSpecialistFoundException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SpecialityException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Specialist Found For Your Issue")
    public ResponseEntity<Object> exception(SpecialityException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoDoctorFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Doctor Found")
    public ResponseEntity<Object> exception(NoDoctorFoundException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = StatusChangeException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Cannot Change Status Of This Appointment")
    public ResponseEntity<Object> exception(StatusChangeException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ScheduleNotFoundException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No Schedule Found For This Exception")
    public ResponseEntity<Object> exception(ScheduleNotFoundException ex) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
