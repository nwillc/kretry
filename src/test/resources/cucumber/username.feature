
Feature: Usernames should be lowercase
  Scenario Outline: Username entered mixed case
    Given a user name is required
    When the user Fred enters mixed case username <entered>
    Then the User username should be <lowercase>

    Examples:
    | entered | lowercase |
    | fred123 | fred123   |
    | Fred123 | fred123   |
    | FRED123 | fred123   |
