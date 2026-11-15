package com.example.studentapi.student;

public record StudentResponse(Long id, String name, String email, Integer age, String course) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getAge(),
                student.getCourse()
        );
    }
}
