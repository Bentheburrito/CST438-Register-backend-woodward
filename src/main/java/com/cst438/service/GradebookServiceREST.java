package com.cst438.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cst438.domain.EnrollmentDTO;


public class GradebookServiceREST extends GradebookService {

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${gradebook.url}")
	String gradebook_url;
	
	public GradebookServiceREST() {
		System.out.println("REST grade book service");
	}

	@Override
	public void enrollStudent(String student_email, String student_name, int course_id) {
		EnrollmentDTO dto = new EnrollmentDTO(student_email, student_name, course_id);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(gradebook_url + "/enrollment");

		HttpEntity<?> entity = new HttpEntity<>(dto, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				builder.toUriString(), 
				HttpMethod.POST, 
				entity, 
				String.class);
		System.out.println("RESPONSE STATUS CODE :: " + response.getStatusCode());
	}

}
