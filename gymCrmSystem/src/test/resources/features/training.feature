Feature: Training API
  Scenario: Create a new Training
    Given I have a valid jwt token from "/api/v1/auth/login" and want to create new Training
    When I send POST request to "/api/v1/trainings"
    Then I should receive 200 response if training was added

    Given I have valid jwt token
    When I send GET request to "/api/v1/trainings/types"
    Then I should receive a list of preloaded types of training