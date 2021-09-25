package com.cowin.scheduler.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);

	public static <Entity> Entity convertToEntityObject(String jsonResponse, Class<Entity> classType) {
		Entity entityObject = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
			objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);


			entityObject = objectMapper.readValue(jsonResponse, classType);
		} catch (IOException e) {
			LOGGER.error("Error while mapping json response to oject");
		}
		return entityObject;
	}

}
