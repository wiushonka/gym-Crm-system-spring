Feature: Authentication API

  Scenario: Successful login returns JWT token
    Given I have valid credentials
    When I send a POST request to login endpoint "/api/v1/auth/login"
    Then I should receive a 200 response
    And the response should contain a JWT tokenlid credintals

  Scenario: Unsuccessful login more than 3 times blocks user
    Given I have invalid credintals username: "sarahw" and password: "mistake"
    When I try to login to endpoint "/api/v1/auth/login" with incorrect credintals many times
    Then after trying it more than 3 timess I should recive message telling me that i got banned