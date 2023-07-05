@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @GENERATE_PIN
Feature: Generate a card PIN
  As the Light authorisation service
  I want to be able to request the generation of a card PIN

  @UUID-2a15b1ea-a2f1-4559-8bec-8e1a24109694 @ECS-4847-AC1 @ECS-15359
  Scenario: Generate a random card PIN
    When a request is made to generate 1 PIN blocks for a card
    Then the request to the security-commands endpoint to request a Pin Generation returns an http status code of 200
    Then the response contains the generated pin blocks
    And the response only contains generate pin fields

  @UUID-4beadcc2-22e9-430d-8914-cce48e3f708c @ECS-4847-AC2 @ECS-5863 @ECS-15359
  Scenario: Generate multiple card PINs
    When a request is made to generate multiple card PINs
    Then the request to the security-commands endpoint to request a Pin Generation returns an http status code of 200
    Then the response contains the generated pin blocks
    And the response only contains generate pin fields
