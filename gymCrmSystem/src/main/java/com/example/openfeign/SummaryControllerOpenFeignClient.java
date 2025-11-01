package com.example.openfeign;

import com.example.config.FeignConfig;
import org.example.trainerworkloadservice.dto.InputDto;
import org.example.trainerworkloadservice.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "trainer-workload-service", configuration = FeignConfig.class)
public interface SummaryControllerOpenFeignClient {
    @PostMapping("/trainers/{username}/trainings")
    ResponseDto addTraining(@PathVariable("username") String username,
                            @RequestBody InputDto input);

    @DeleteMapping("/trainers/{username}/trainings")
    ResponseDto deleteTraining(@PathVariable("username") String username,
                               @RequestBody InputDto input);

    @GetMapping("/trainers/{username}/summary")
    ResponseDto getSummary(@PathVariable("username") String username);
}
