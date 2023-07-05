@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @GENERATE_PIN_OFFSETS
Feature: Generate Pin Offsets
  As a Product Owner
  I want to be able to request the generation of Pin Offsets

  @UUID-d343faf4-3990-47bf-a256-5946de9057df @ECS-15361
  Scenario: Generate a card PIN offset
    Given 1 PVK key created on the Payment Security service
    When a request is made to generate a card PIN offset using the given number of keys
    Then the request to the security-commands endpoint to request a Pin Offset Generation returns an http status code of 200
    Then the generated PIN offset response contains the expected values
    And the response only contains generate pin offset fields

  @UUID-586be368-37a0-4950-a30b-8715595f51e8 @ECS-15361
  Scenario: Generate multiple card PIN offsets
    Given 2 PVK keys created on the Payment Security service
    When a request is made to generate a card PIN offset using the given number of keys
    Then the request to the security-commands endpoint to request a Pin Offset Generation returns an http status code of 200
    Then the generated PIN offset response contains the expected values
    And the response only contains generate pin offset fields
