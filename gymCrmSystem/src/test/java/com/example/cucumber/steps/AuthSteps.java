package com.example.cucumber.steps;

import io.cucumber.java.en.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AuthSteps {

    private static final Logger log = LoggerFactory.getLogger(AuthSteps.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    @LocalServerPort
    private int port;
    private String username;
    private String password;
    private Response resp;

    @Given("I have valid credentials")
    public void i_have_valid_credentials() {
        username = "johndoe";
        password = "pass123";
    }

    @When("I send a POST request to login endpoint {string}")
    public void i_send_a_post_request_to_login_endpoint(String endpoint) {
        String requestBody = "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        response = restTemplate.postForEntity("http://localhost:"+ port + endpoint, request, String.class);
    }

    @Then("I should receive a 200 response")
    public void i_should_receive_a_200_response() {
        assertEquals(200, response.getStatusCodeValue());
    }

    @Then("the response should contain a JWT tokenlid credintals")
    public void the_response_should_contain_a_jwt_tokenlid_credintals() {
        String body = response.getBody();
        assertNotNull(body);
        String jwtToken = body.substring(body.indexOf(":\"") + 2, body.lastIndexOf("\""));
        assertTrue(jwtToken.length() > 0);
        System.out.println("JWT token: " + jwtToken);
    }

    @Given("I have invalid credintals username: {string} and password: {string}")
    public void i_have_invalid_credintals(String testUsername, String testIncorrectPassword) {
        username = testUsername;
        password = testIncorrectPassword;
    }

    @When("I try to login to endpoint {string} with incorrect credintals many times")
    public void i_try_to_login_to_endpoint_with_incorrect_credintals(String endpoint) {
        Map<String,String> invalidCredintals = Map.of(
            "username",username,
            "password",password
        );

        for(int i=0;i<=4;++i){
            resp = RestAssured.given().contentType("application/json")
                    .body(invalidCredintals)
                    .post("http://localhost:"+ port + endpoint);
        }
    }

    @Then("after trying it more than 3 timess I should recive message telling me that i got banned")
    public void did_i_get_banned_or_not() {
        log.info(resp.prettyPrint());
        String message = resp.jsonPath().getString("message");
        assertTrue(message.contains("locked"));
    }
}
