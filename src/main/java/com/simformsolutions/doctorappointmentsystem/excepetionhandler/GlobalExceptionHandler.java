package com.simformsolutions.doctorappointmentsystem.excepetionhandler;

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
                constraintViolation -> errors.put(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessageTemplate())
        );
        return errors;
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> exception(DataIntegrityViolationException exception) {
        return new ResponseEntity<>("Email Already Exists", HttpStatus.CONFLICT);
    }



    @ExceptionHandler(value = NoDoctorAvailableExcepetion.class)
    public ResponseEntity<Object> exception(NoDoctorAvailableExcepetion ex){
        return  new ResponseEntity<>("No Doctor Available For This Date",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoAppointmentFoundException.class)
    public ResponseEntity<Object> exception(NoAppointmentFoundException ex){
        return  new ResponseEntity<>("No Appoinment Found With This ID",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> exception(UserNotFoundException ex){
        return  new ResponseEntity<>("No User Found With This ID",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSpecialistFoundExcpetion.class)
    public ResponseEntity<Object> exception(NoSpecialistFoundExcpetion ex){
        return  new ResponseEntity<>("Cannot Find Specialist For Your Issue",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SpecialityException.class)
    public ResponseEntity<Object> exception(SpecialityException ex){
        return  new ResponseEntity<>("No Specialist Found For Your Issue",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoDoctorFoundException.class)
    public ResponseEntity<Object> exception(NoDoctorFoundException ex){
        return  new ResponseEntity<>("No Doctor Found",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = StatusChangeException.class)
    public ResponseEntity<Object> exception(StatusChangeException ex){
        return  new ResponseEntity<>("Cannot Change Status Of This Appointment",HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ScheduleNotFoundException.class)
    public ResponseEntity<Object> exception(ScheduleNotFoundException ex){
        return  new ResponseEntity<>("No Schedule Found For This Exception",HttpStatus.BAD_REQUEST);
    }

}
