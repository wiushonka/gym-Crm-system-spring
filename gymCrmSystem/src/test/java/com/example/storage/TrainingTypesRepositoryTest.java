package com.example.storage;

import com.example.storage.entitys.training.TrainingType;
import com.example.storage.repos.trainingtype.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TrainingTypesRepositoryTest {

    @Autowired
    private TrainingTypeRepository repo;

    @Test
    public void testInitiallyLoadedTrainingTypes() {
        List<TrainingType> lst = repo.findAllTrainingTypes();
        assertEquals(10,lst.size());

        Optional<TrainingType> type = repo.findByName(lst.getFirst().getTrainingTypeName());
        assertTrue(type.isPresent());

        Optional<TrainingType> nonExistingType = repo.findByName("bingus");
        assertTrue(nonExistingType.isEmpty());
    }
}
