package com.example.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Map;

public class TrainerSteps {

    @LocalServerPort
    private int port;
    private Response response;
    private String token;
    private Map<String,String> validTrainerRegistrationPayload;

    private static final Logger log = LoggerFactory.getLogger(TrainerSteps.class);

    @Given("I have a valid Trainer registration payload")
    public void i_have_a_valid_Trainer_registration_payload() {
        validTrainerRegistrationPayload = Map.of(
                "firstName","test",
                "lastName","user1",
                "specialization","zumba"
        );
    }

    @When("I send a POST request to trainer registration endpoint {string}")
    public void i_send_a_post_request_to_trainer_registration_endpoint(String endpoint) {
        response = RestAssured.given()
                .contentType("application/json")
                .body(validTrainerRegistrationPayload)
                .post("http://localhost:" + port + endpoint);

        log.info(response.getBody().prettyPrint());
    }

    @Then("I should receive response with username, bearer token and encrypted password")
    public void i_should_receive_response_with_username_and_encrypted_password () {
        token = response.jsonPath().getString("bearerToken");
        String encryptedPassword = response.jsonPath().getString("password");
        String username = response.jsonPath().getString("username");

        assertThat(response,notNullValue());

        assertThat(token,notNullValue());
        assertThat(encryptedPassword,notNullValue());
        assertThat(username,notNullValue());

        assertEquals("test.user1",username);
        assertTrue(encryptedPassword.length()>10);
    }

    @Given("I have a valid bearer token and I try to change my password using bearer token")
    public void i_have_a_valid_bearer_token_and_i_try_to_change_my_password_using_bearer_token() {
        log.info("bearer token: {}", token);
    }

    @When("I send PUT request on password update endpoint {string} with new password: {string}")
    public void i_send_put_request_on_password_update_endpoint_with_new_password(String endpoint, String password) {
        Map<String,String> passwordUpdatePayload = Map.of(
                "username","test.user1",
                "newPassword","pass123"
        );

        response = RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(passwordUpdatePayload)
                .put("http://localhost:" + port + endpoint);
    }

    @Then("I should receive 200 response if password was changed successfully")
    public void i_should_receive_200_response_if_password_was_changed_successfully() {
        log.info(response.prettyPrint());
        assertThat(response,notNullValue());
        assertThat(response.getStatusCode(),equalTo(200));
    }

    @Given("Password changed")
    public void password_changed() {
        log.info("password changed => i should be able to login with new password");
    }

    @When("I want to login on {string} with my new credintals username : {string} and password : {string}")
    public void i_want_to_try_logging_in_with_new_credintals_username_and_password(String endpoint ,String newUsername, String newPassword) {
        Map<String,String> updatedCredintals = Map.of(
            "username","test.user1",
            "password","pass123"
        );

        response = RestAssured.given().contentType("application/json")
                .body(updatedCredintals)
                .post("http://localhost:" + port + endpoint);
    }

    @Then("I should be able to log in and receive bearer token")
    public void i_should_log_in_and_receive_bearer_token() {
        log.info(response.prettyPrint());
        assertThat(response,notNullValue());
        assertThat(response.getStatusCode(),equalTo(200));
        assertThat(response.jsonPath().getString("token").length()>15,equalTo(true));
    }

    @Given("I want to find status of colleague but typed username incorrectly")
    public void I_am_searching_incorrect_name(){}

    @When("I send GET request to trainer endpoint {string}")
    public void i_send_get_request_to_trainer_endpoint(String endpoint) {
        response = RestAssured.given().contentType("application/json")
                .header("Authorization","Bearer " + token)
                .get("http://localhost:" + port + endpoint);
        assertThat(response,notNullValue());
    }

    @Then("I should receive error response 404 and appropriate message")
    public void i_should_receive_error_response_404_and_appropriate_message() {
        log.info(response.prettyPrint());
        assertEquals(404,response.getStatusCode());
    }
}
