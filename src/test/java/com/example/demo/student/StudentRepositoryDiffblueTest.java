package com.example.demo.student;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {StudentRepository.class})
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.example.demo.student"})
@DataJpaTest(properties = {"spring.main.allow-bean-definition-overriding=true"})
class StudentRepositoryDiffblueTest {
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Method under test: {@link StudentRepository#selectExistsEmail(String)}
     */
    @Test
    void testSelectExistsEmail() {
        // Arrange
        Student student = new Student();
        student.setEmail("jane.doe@example.org");
        student.setGender(Gender.MALE);
        student.setName("Name");

        Student student2 = new Student();
        student2.setEmail("john.smith@example.org");
        student2.setGender(Gender.FEMALE);
        student2.setName("com.example.demo.student.Student");
        studentRepository.save(student);
        studentRepository.save(student2);

        // Act and Assert
        assertTrue(studentRepository.selectExistsEmail("jane.doe@example.org"));
    }

    /**
     * Method under test: {@link StudentRepository#selectExistsEmail(String)}
     */
    @Test
    void testSelectExistsEmail2() {
        // Arrange
        Student student = new Student();
        student.setEmail("prof.einstein@example.org");
        student.setGender(Gender.MALE);
        student.setName("Name");

        Student student2 = new Student();
        student2.setEmail("john.smith@example.org");
        student2.setGender(Gender.FEMALE);
        student2.setName("com.example.demo.student.Student");
        studentRepository.save(student);
        studentRepository.save(student2);

        // Act and Assert
        assertFalse(studentRepository.selectExistsEmail("jane.doe@example.org"));
    }
}
