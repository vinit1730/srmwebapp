package com.example.studentapi.student;

public class DuplicateStudentEmailException extends RuntimeException {

    public DuplicateStudentEmailException(String email) {
        super("A student with email " + email + " already exists.");
    }
}
