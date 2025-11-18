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
    private String username;
    private String password;
    private String incorrectUsername;
    private String updatedPassword;

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


    @Given("I have valid credintals for a trainee username: {string} and password {string}")
    public void i_have_valid_credintals(String inpUsername, String inpPassword) {
        username = inpUsername;
        password = inpPassword;
    }

    @When("I try to login to endpoint {string} with my valid credintals")
    public void i_try_to_login_to_endpoint_with_my_valid_credintals(String endpoint) {
        Map<String, String> credentials = Map.of("username", username, "password", password);
        response = RestAssured.given().contentType("application/json")
                .body(credentials)
                .post("http://localhost:" + port + endpoint);
    }

    @Then("I should receive response with bearer Token")
    public void i_should_receive_response_with_bearer_Token() {
        log.info(response.prettyPrint());
        assertThat(response.getStatusCode(), equalTo(200));
        tokenShmoken = response.jsonPath().getString("token");
    }

    @Given("I want to update pasword for trainee username: {string} to {string}")
    public void i_want_to_update_password_for_trainee_username_to(String nonExistentUsername, String newPassword) {
        incorrectUsername=nonExistentUsername;
        updatedPassword=newPassword;
    }

    @When("I send PUT request with invalid credintals to endpoint {string}")
    public void i_send_put_request_with_invalid_credintals(String endpoint) {
        Map<String,String> credentials = Map.of("username", incorrectUsername, "newPassword", updatedPassword);
        response = RestAssured.given().contentType("application/json")
                .header("Authorization", "Bearer " + tokenShmoken)
                .body(credentials)
                .put("http://localhost:" + port + endpoint);

        assertThat(response,notNullValue());
    }

    @Then("I shall receive error code 401, because authentication check will kick me out before invalid password check" +
            ", which actually wont get invoked at all")
    public void i_shall_be_kicked_out(){
        log.info(response.prettyPrint());
        assertThat(response.getStatusCode(),equalTo(401));
    }

    @Given("I want check profile of user: some non existent user")
    public void i_want_check_profile_of_user_some_non_existent_user() {}

    @When("I send GET request to {string} in order to get retrieve non existent user")
    public void i_send_get_request_to(String endpoint) {
        response = RestAssured.given().contentType("application/json")
                .header("Authorization","Bearer " + tokenShmoken)
                .get("http://localhost:"+ port + endpoint);
    }

    @Then("I should receive response code 404 and logical message")
    public void i_should_receive_response_code_404_and_logical_message() {
        assertThat(response.getStatusCode(),equalTo(404));
        assertThat(response.getBody(),notNullValue());
        log.info(response.prettyPrint());
    }
}
