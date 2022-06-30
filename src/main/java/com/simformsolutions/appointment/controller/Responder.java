package com.simformsolutions.appointment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Responder<T> {
    private static final Logger log = LoggerFactory.getLogger(Responder.class);

    public ResponseEntity<T> apply(T dto) {
        log.info("{}", dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}