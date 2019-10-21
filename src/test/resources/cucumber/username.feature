
Feature: Usernames should be lowercase
  Scenario: Username entered mixed case
    Given a username is required
    When the user Fred enters mixed case username Fred123
    Then the User username should be fred123
