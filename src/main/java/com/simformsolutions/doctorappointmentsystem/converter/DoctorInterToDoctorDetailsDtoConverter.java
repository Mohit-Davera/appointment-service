/*
package com.simformsolutions.doctorappointmentsystem.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.simformsolutions.doctorappointmentsystem.Projection.DoctorDetailsDto;
import com.simformsolutions.doctorappointmentsystem.Projection.DoctorInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class DoctorInterToDoctorDetailsDtoConverter implements Function<DoctorInter, DoctorDetailsDto> {
	private static final Logger log = LoggerFactory.getLogger(DoctorInterToDoctorDetailsDtoConverter.class);

	private final ObjectMapper objectMapper;

	public DoctorInterToDoctorDetailsDtoConverter(ObjectMapper objectMapper) {
		this.objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}


	@Override
	public DoctorDetailsDto apply(DoctorInter doctorInter) {
		try {
			final String jsonString = objectMapper.writeValueAsString(doctorInter);
			return objectMapper.readValue(jsonString, DoctorDetailsDto.class);
		} catch (JsonProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
}
*/
