package com.cst438;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.cst438.controller.ScheduleController;
import com.cst438.controller.StudentController;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {

	// static final String URL = "http://localhost:3000";
	public static final int TEST_STUDENT_ID = 112233;
	public static final String TEST_STUDENT_EMAIL = "iamatest@gmail.com";
	public static final String TEST_STUDENT_NAME = "Test";
	public static final int UPDATED_STATUS_CODE = 1;
	public static final String UPDATED_STATUS = "Can not register";

	@MockBean
	CourseRepository courseRepository;

	@MockBean
	StudentRepository studentRepository;

	@MockBean
	EnrollmentRepository enrollmentRepository;

	@MockBean
	GradebookService gradebookService;

	@Autowired
	private MockMvc mvc;

	@Test
	public void addStudent() throws Exception {

		MockHttpServletResponse response;

		Student student = new Student();
		student.setStudent_id(TEST_STUDENT_ID);
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);

		// given  -- stubs for database repositories that return test data
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		given(studentRepository.save(any(Student.class))).willReturn(student);

		// create the DTO (data transfer object) for the student to add.
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_id = TEST_STUDENT_ID;
		studentDTO.email = TEST_STUDENT_EMAIL;
		studentDTO.name = TEST_STUDENT_NAME;
		studentDTO.status_code = 0;
		studentDTO.status = "Can register";

		// then do an http post request with body of studentDTO as JSON
		response = mvc.perform(
				MockMvcRequestBuilders
						.post("/student")
						.content(asJsonString(studentDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());

		// verify that returned data has non zero primary key
		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertNotEquals(0, result.student_id);


		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
	}

	@Test
	public void updateStudentStatus() throws Exception {
		MockHttpServletResponse response;

		Student expectedStudent = new Student();
		expectedStudent.setStudent_id(TEST_STUDENT_ID);
		expectedStudent.setEmail(TEST_STUDENT_EMAIL);
		expectedStudent.setName(TEST_STUDENT_NAME);
		expectedStudent.setStatusCode(0);
		expectedStudent.setStatus("Can not register");

		// given  -- stubs for database repositories that return test data
		given(studentRepository.findById(TEST_STUDENT_ID)).willReturn(Optional.of(expectedStudent));
		expectedStudent.setStatusCode(1);
		expectedStudent.setStatus("Can not register");
		given(studentRepository.save(any(Student.class))).willReturn(expectedStudent);

		// create the DTO (data transfer object) for the student to add.
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_id = TEST_STUDENT_ID;
		studentDTO.status_code = UPDATED_STATUS_CODE;
		studentDTO.status = UPDATED_STATUS;

		System.out.println("AS JSON STRING: " + asJsonString(studentDTO));

		// then do an http post request with body of studentDTO as JSON
		response = mvc.perform(
				MockMvcRequestBuilders
						.put("/student")
						.content(asJsonString(studentDTO))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();

		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());

		// verify that returned data has non zero primary key
		StudentDTO result = fromJsonString(response.getContentAsString(), StudentDTO.class);
		assertNotEquals(0, result.student_id);

		assertEquals(result.status, UPDATED_STATUS);
		assertEquals(result.status_code, UPDATED_STATUS_CODE);

		// verify that repository save method was called.
		verify(studentRepository).save(any(Student.class));
	}
	
	private static String asJsonString(final Object obj) {
		try {

			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
