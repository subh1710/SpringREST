package com.subh.springdemo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.subh.springdemo.entity.Student;
import com.subh.springdemo.service.StudentService;

@RestController
@RequestMapping("/api")
public class StudentRestController {

	@Autowired
	private StudentService studentService;

	private List<Student> students;

	@GetMapping("/students")
	public List<Student> getStudents() {
		return studentService.getStudents();
	}

	@GetMapping("/students/{studentId}")
	public Student getStudent(@PathVariable int studentId) {
		Student student = studentService.getStudent(studentId);
		if (student == null) {
			throw new StudentNotFoundException("Student not found with ID: " + studentId);
		}
		return student;
	}

	@PostMapping("/students")
	public Student addStudent(@RequestBody Student student) {
		student.setId(0);
		studentService.saveStudent(student);
		return student;
	}

	@PutMapping("/students")
	public Student updateStudent(@RequestBody Student student) {
		studentService.saveStudent(student);
		return student;
	}

	@DeleteMapping("/students/{studentId}")
	public String deleteCustomer(@PathVariable int studentId) {
		Student tempStudent = studentService.getStudent(studentId);
		if (tempStudent == null) {
			throw new StudentNotFoundException("Student not found with ID: " + studentId);
		}
		studentService.deleteStudent(studentId);
		return "Student deleted successfully with ID: " + studentId;
	}
	
	@PatchMapping(path = "/students/{studentId}", consumes = "application/json-patch+json")
	public ResponseEntity<Student> updateStudent(@PathVariable int studentId, @RequestBody JsonPatch patch) throws JsonProcessingException, JsonPatchException {
		Student tempStudent = studentService.getStudent(studentId);
		if (tempStudent == null) {
			throw new StudentNotFoundException("Student not found with ID: " + studentId);
		}
		PatchHelper patchHelper = new PatchHelper();
		Student studentPatched = patchHelper.applyPatchToStudent(patch, tempStudent);
		studentService.saveStudent(studentPatched);
		return ResponseEntity.ok(studentPatched);
	}
	
	@RequestMapping(value = "/students", method = RequestMethod.OPTIONS)
	public ResponseEntity<?> collectionOptions(){
		return ResponseEntity
                .ok()
                .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.OPTIONS)
                .build();
	}

	/*
	 * @PatchMapping(path = "/students/{studentId}", consumes =
	 * "application/merge-patch+json") public ResponseEntity<Void>
	 * updateContact(@PathVariable int studentId,
	 * 
	 * @RequestBody JsonMergePatch mergePatchDocument) {
	 * 
	 * // Find the model that will be patched // Contact contact =
	 * contactService.findContact(id).orElseThrow(ResourceNotFoundException::new);
	 * Student tempStudent = studentService.getStudent(studentId); if (tempStudent
	 * == null) { throw new StudentNotFoundException("Student not found with ID: " +
	 * studentId); }
	 * 
	 * // Apply the patch // Contact contactPatched = mergePatch(mergePatchDocument,
	 * contact, Contact.class); PatchHelper patchHelper=new PatchHelper(); Student
	 * studentPatched = patchHelper.mergePatch(mergePatchDocument, tempStudent,
	 * Student.class);
	 * 
	 * // Persist the changes // contactService.updateContact(contactPatched);
	 * studentService.saveStudent(studentPatched);
	 * 
	 * // Return 204 to indicate the request has succeeded return
	 * ResponseEntity.noContent().build(); }
	 */


}
