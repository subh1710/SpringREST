package com.subh.springdemo.rest;

import java.util.Set;

import javax.json.JsonMergePatch;
import javax.json.JsonValue;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.subh.springdemo.entity.Student;

@Component
public class PatchHelper {

	private final ObjectMapper mapper;
//	private final Validator validator;

//	public PatchHelper(Validator validator) {
	public PatchHelper() {
//		this.validator = validator;
		this.mapper = new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).findAndRegisterModules()
				.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	}

	Student applyPatchToStudent(JsonPatch patch, Student targetStudent)
			throws JsonPatchException, JsonProcessingException {
		JsonNode patched = patch.apply(mapper.convertValue(targetStudent, JsonNode.class));
		return mapper.treeToValue(patched, Student.class);
	}

	public <T> T mergePatch(JsonMergePatch mergePatch, T targetBean, Class<T> beanClass) {
		JsonValue target = mapper.convertValue(targetBean, JsonValue.class);
		JsonValue patched = applyMergePatch(mergePatch, target);
		return convertAndValidate(patched, beanClass);
	}

	private JsonValue applyMergePatch(JsonMergePatch mergePatch, JsonValue target) {
		try {
			return mergePatch.apply(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private <T> T convertAndValidate(JsonValue jsonValue, Class<T> beanClass) {
		T bean = mapper.convertValue(jsonValue, beanClass);
//		validate(bean);
		return bean;
	}

	/*
	 * private <T> void validate(T bean) { Set<ConstraintViolation<T>> violations =
	 * validator.validate(bean); if (!violations.isEmpty()) { throw new
	 * ConstraintViolationException(violations); } }
	 */
}
