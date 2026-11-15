package com.example.studentapi.student;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAll() {
        return studentRepository.findAllByOrderByIdAsc()
                .stream()
                .map(StudentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return StudentResponse.from(getStudent(id));
    }

    public StudentResponse create(StudentRequest request) {
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, null);

        Student student = new Student(
                request.name().trim(),
                email,
                request.age(),
                request.course().trim()
        );
        return StudentResponse.from(studentRepository.save(student));
    }

    public StudentResponse update(Long id, StudentRequest request) {
        Student student = getStudent(id);
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, id);

        student.update(
                request.name().trim(),
                email,
                request.age(),
                request.course().trim()
        );
        return StudentResponse.from(studentRepository.save(student));
    }

    public void delete(Long id) {
        studentRepository.delete(getStudent(id));
    }

    private Student getStudent(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    private void ensureEmailAvailable(String email, Long studentId) {
        boolean alreadyUsed = studentId == null
                ? studentRepository.existsByEmailIgnoreCase(email)
                : studentRepository.existsByEmailIgnoreCaseAndIdNot(email, studentId);
        if (alreadyUsed) {
            throw new DuplicateStudentEmailException(email);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
