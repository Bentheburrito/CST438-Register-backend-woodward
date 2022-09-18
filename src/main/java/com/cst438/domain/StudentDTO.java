package com.cst438.domain;

public class StudentDTO {

	public int student_id;
	public String email;
	public String name;
	public String status;
	public int status_code;
	
	@Override
	public String toString() {
		return "StudentDTO [name=" + name + ", email=" + email + ", student_id=" +student_id + ", status=" + status + ", status_code=" + status_code + "]";
	}
}
