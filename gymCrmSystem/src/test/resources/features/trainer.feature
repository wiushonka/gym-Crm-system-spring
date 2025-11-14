Feature: Trainer API
  Scenario: Create a new Trainer profile
    Given I have a valid Trainer registration payload
    When I send a POST request to trainer registration endpoint "/api/v1/trainers"
    Then I should receive response with username, bearer token and encrypted password

    Given I have a valid bearer token and I try to change my password using bearer token
    When I send PUT request on password update endpoint "/api/v1/trainers/test.user1/password" with new password: "pass123"
    Then I should receive 200 response if password was changed successfully

    Given Password changed
    When I want to login on "/api/v1/auth/login" with my new credintals username : "test.user1" and password : "pass123"
    Then I should be able to log in and receive bearer token