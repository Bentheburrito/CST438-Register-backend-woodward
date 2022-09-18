package com.cst438.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;

@RestController
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;

	/*
	 * saves a new student to the DB if their email does not already exist.
	 */
	@PostMapping("/student")
	@Transactional
	public StudentDTO saveNewStudent( @RequestBody StudentDTO studentDTO) {
		if (studentRepository.findByEmail(studentDTO.email) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "A student with that email already exists.  " + studentDTO.email);
		} else {
			Student student = new Student();
			student.setEmail(studentDTO.email);
			student.setName(studentDTO.name);
			student.setStatus(studentDTO.status);
			student.setStatusCode(studentDTO.status_code);
			System.out.println(student.toString());
			Student savedStudent = studentRepository.save(student);
			System.out.println("???? " + (savedStudent == null));
			return createStudentDTO(savedStudent);
		}
	}

	@PutMapping("/student")
	@Transactional
	public StudentDTO updateStudentStatus(@RequestBody StudentDTO studentDTO) {

		Optional<Student> maybe_student = studentRepository.findById(studentDTO.student_id);
		if (maybe_student.isPresent()) {
			Student student = maybe_student.get();
			student.setStatus(studentDTO.status);
			student.setStatusCode(studentDTO.status_code);
			return createStudentDTO(studentRepository.save(student));
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"A student does not exist by that ID.  " + studentDTO.email);
		}
	}
	
	private StudentDTO createStudentDTO(Student student) {
		StudentDTO studentDTO = new StudentDTO();
		studentDTO.student_id = student.getStudent_id();
		studentDTO.email = student.getEmail();
		studentDTO.name = student.getName();
		studentDTO.status_code = student.getStatusCode();
		studentDTO.status = student.getStatus();
		return studentDTO;
	}
}
