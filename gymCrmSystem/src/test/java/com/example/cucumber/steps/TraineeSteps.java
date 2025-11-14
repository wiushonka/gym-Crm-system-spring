package com.example.cucumber.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TraineeSteps {

    private static final Logger log = LoggerFactory.getLogger(TraineeSteps.class);

    @LocalServerPort
    private int port;
    private Response response;
    private String tokenShmoken;

    @Given("I have a valid trainee registration payload")
    public void i_have_a_valid_trainee_registration_payload() {
    }

    @When("I send a POST request to trainee endpoint {string}")
    public void i_send_a_post_request_to_trainee_endpoint(String endpoint) {
        Map<String, String> trainee = Map.of(
                "firstName", "test",
                "lastName", "User",
                "birthDate", "2004-09-09",
                "address", "testPass"
        );

        response = RestAssured.given()
                .contentType("application/json")
                .body(trainee)
                .post("http://localhost:" + port + endpoint);

        log.info(response.getBody().asString());

        tokenShmoken = response.jsonPath().getString("bearerToken");

        log.info("retrieved token shmoken: {}", tokenShmoken);
    }

    @Given("I have valid bearerToken")
    public void i_just_print_bearerToken_here() {
        assertThat(tokenShmoken, notNullValue());
        log.info("bearer token: {}", tokenShmoken);
    }

    @When("I send a GET request to trainee endpoint {string} with bearerToken")
    public void i_send_a_get_request_to_trainee_with_bearerToken(String endpoint) {
        response = RestAssured.given()
                .header("Authorization","Bearer " + tokenShmoken)
                .get("http://localhost:" + port + endpoint);
    }

    @Then("I should receive a {int} shmesponce")
    public void i_should_receive_a_shmesponce(Integer expectedStatus) {
        assertThat(response.getStatusCode(), equalTo(expectedStatus));
    }
}
