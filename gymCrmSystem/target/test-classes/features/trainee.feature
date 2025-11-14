Feature: Trainee API

  Scenario: Create a new trainee profile
    Given I have a valid trainee registration payload
    When I send a POST request to trainee endpoint "/api/v1/trainees"
    Then I should receive a 201 shmesponce

    Given I have valid bearerToken
    When I send a GET request to trainee endpoint "/api/v1/trainees/test.user" with bearerToken
    Then I should receive a 200 shmesponce