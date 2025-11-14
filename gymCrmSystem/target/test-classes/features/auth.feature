Feature: Authentication API

  Scenario: Successful login returns JWT token
    Given I have valid credentials
    When I send a POST request to login endpoint "/api/v1/auth/login"
    Then I should receive a 200 response
    And the response should contain a JWT tokenlid credintals
