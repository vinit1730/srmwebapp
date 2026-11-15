package com.example.studentapi.student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void clearStudents() {
        studentRepository.deleteAll();
    }

    @Test
    void createsAndReadsStudent() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Ada Lovelace",
                                  "email": "Ada@example.com",
                                  "age": 20,
                                  "course": "Computer Science"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/students/")))
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.id").isNumber());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Ada Lovelace"));
    }

    @Test
    void rejectsInvalidAndDuplicateStudentData() throws Exception {
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "",
                                  "email": "not-an-email",
                                  "age": 0,
                                  "course": "Computer Science"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.age").exists());

        String student = """
                {
                  "name": "Grace Hopper",
                  "email": "grace@example.com",
                  "age": 25,
                  "course": "Mathematics"
                }
                """;
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(student.replace("grace@example.com", "GRACE@example.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void updatesExistingStudent() throws Exception {
        Student student = studentRepository.save(
                new Student("Katherine Johnson", "katherine@example.com", 30, "Physics")
        );

        mockMvc.perform(put("/api/students/" + student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Katherine Johnson",
                                  "email": "KATHERINE@example.com",
                                  "age": 31,
                                  "course": "Mathematics"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.age").value(31))
                .andExpect(jsonPath("$.course").value("Mathematics"))
                .andExpect(jsonPath("$.email").value("katherine@example.com"));
    }

    @Test
    void returnsNotFoundForMissingStudentAndDeletesExistingStudent() throws Exception {
        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound());

        Student student = studentRepository.save(
                new Student("Katherine Johnson", "katherine@example.com", 30, "Physics")
        );

        mockMvc.perform(delete("/api/students/" + student.getId()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/students/" + student.getId()))
                .andExpect(status().isNotFound());
    }
}
