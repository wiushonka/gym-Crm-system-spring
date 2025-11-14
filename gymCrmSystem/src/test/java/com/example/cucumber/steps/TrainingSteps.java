package com.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class TrainingSteps {
    private static final Logger log = LoggerFactory.getLogger(TrainingSteps.class);
    @LocalServerPort
    private int port;
    private Response response;
    private String token;

    @Given("I have a valid jwt token from {string} and want to create new Training")
    public void i_have_a_valid_jwt_token_and_want_to_create_new_Training(String endpoint){
        Map<String,String> validCredentials = Map.of(
                "username","johndoe",
                "password","pass123"
        );

        response = RestAssured.given()
                .contentType("application/json")
                .body(validCredentials)
                .post("http://localhost:" + port + endpoint);

        log.info(response.getBody().prettyPrint());
        assertThat(response,notNullValue());
        token = response.jsonPath().getString("token");
        assertThat(token.length()>10,equalTo(true));
    }

    @When("I send POST request to {string}")
    public void i_send_post_request_to(String endpoint){
        Map<String,String> validNewTrainingDto = Map.of(
                "traineeUsername","johndoe",
                "trainerUsername","maxwilliams",
                "trainingName","max zumba course",
                "trainingStartDate","2011-09-09",
                "trainingDuration","100"
        );

        response = RestAssured.given().contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(validNewTrainingDto)
                .post("http://localhost:" + port + endpoint);
    }

    @Then("I should receive 200 response if training was added")
    public void i_should_receive_200_response_if_training_was_added() {
        assertThat(response,notNullValue());
        assertThat(response.statusCode(),equalTo(200));
    }

    @Given("I have valid jwt token")
    public void i_have_valid_jwt_token() {
        log.info("I have a valid jwt token: {}",token);
    }

    @When("I send GET request to {string}")
    public void i_send_get_request_to(String endpoint){
        response = RestAssured.given().contentType("application/json")
                .header("Authorization","Bearer " + token)
                .get("http://localhost:" + port + endpoint);
    }

    @Then("I should receive a list of preloaded types of training")
    public void i_should_get_normal_response(){
        log.info(response.getBody().prettyPrint());
        assertThat(response,notNullValue());
    }
}
