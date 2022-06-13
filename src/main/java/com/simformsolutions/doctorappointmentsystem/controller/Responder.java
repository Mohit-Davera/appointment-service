package com.simformsolutions.doctorappointmentsystem.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Responder<T> {
    private static final Logger log = LoggerFactory.getLogger(Responder.class);

    public ResponseEntity<T> apply(T dto) {
        log.info(dto.toString());
        return new ResponseEntity<T>(dto, HttpStatus.OK);
    }
}