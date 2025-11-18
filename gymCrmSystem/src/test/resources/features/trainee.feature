Feature: Trainee API

  Scenario: Create a new trainee profile
    Given I have a valid trainee registration payload
    When I send a POST request to trainee endpoint "/api/v1/trainees"
    Then I should receive a 201 shmesponce

    Given I have valid bearerToken
    When I send a GET request to trainee endpoint "/api/v1/trainees/test.user" with bearerToken
    Then I should receive a 200 shmesponce

  Scenario:
    Given I have valid credintals for a trainee username: "johndoe" and password "pass123"
    When I try to login to endpoint "/api/v1/auth/login" with my valid credintals
    Then I should receive response with bearer Token

    Given I want to update pasword for trainee username: "blabla" to "blablabla"
    When I send PUT request with invalid credintals to endpoint "/api/v1/trainees/blabla/password"
    Then I shall receive error code 401, because authentication check will kick me out before invalid password check, which actually wont get invoked at all

    Given I want check profile of user: some non existent user
    When I send GET request to "/api/v1/trainees/blabla" in order to get retrieve non existent user
    Then I should receive response code 404 and logical message