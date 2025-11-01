package com.example.utili;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class UtiliTest {

    @Test
    void testGenerateUsernameWithoutSerial() {
        Predicate<String> exists = name -> false;
        String firstName = "John";
        String lastName = "Doe";

        String username = Utili.generateUsername(firstName, lastName, exists);

        assertEquals("John.Doe", username);
    }

    @Test
    void testGenerateUsernameWithDuplicates() {
        Set<String> existing = new HashSet<>();
        existing.add("John.Doe");
        existing.add("John.Doe1");

        Predicate<String> exists = existing::contains;

        String username = Utili.generateUsername("John", "Doe", exists);

        assertEquals("John.Doe2", username);
    }

    @Test
    void testGeneratePasswordLength() {
        String password = Utili.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testGeneratePasswordCharacters() {
        String password = Utili.generatePassword();
        String allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (char c : password.toCharArray()) {
            assertTrue(allowedChars.indexOf(c) >= 0, "Password contains invalid character: " + c);
        }
    }

    @Test
    void testGeneratePasswordRandomness() {
        String password1 = Utili.generatePassword();
        String password2 = Utili.generatePassword();

        assertNotEquals(password1, password2, "Two generated passwords should not be equal");
    }

    @Test
    void testGenerateUniqueNameForTrainingWithoutDuplicate() {
        Predicate<String> exists = name -> false;
        String name = "Yoga";

        String uniqueName = Utili.generateUniqueNameForTraining(name, exists);
        assertEquals("Yoga", uniqueName);
    }

    @Test
    void testGenerateUniqueNameForTrainingWithDuplicates() {
        Set<String> existing = new HashSet<>();
        existing.add("Yoga");
        existing.add("Yoga_1");

        Predicate<String> exists = existing::contains;

        String uniqueName = Utili.generateUniqueNameForTraining("Yoga", exists);
        assertEquals("Yoga_2", uniqueName);
    }
}
