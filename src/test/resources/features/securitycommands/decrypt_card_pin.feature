@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @DECRYPT_PIN
Feature: Decrypt a card PIN
  As the Light authorisation service
  I want to be able to request the decryption of a card PIN

  @UUID-561c5234-3ec3-41ce-ac6c-52d287bc960a @ECS-4846 @ECS-15356
  Scenario: 01 - Decrypt a card PIN
    Given a request is made to generate 1 PIN blocks for a card
    When a request is made to decrypt the card PIN
    Then the request to the security-commands endpoint to request a Pin Decryption returns an http status code of 200
    Then the response contains the clear PIN value
    And the response only contains decrypt pin fields

  @UUID-715d6bfe-b255-4ce3-a491-c2492504fd91 @ECS-6371 @ECS-15356
  Scenario: 02 - Decrypt a card PIN encrypted by Variant LMK
    Given a card PIN block was was generated through the Payment Security service using a Variant key
    When a request is made to decrypt the Variant card PIN
    And the decrypt card pin error message contains "Execution of at least one security command has failed. Aborting the entire batch of commands" and the error code is "SECURITY_COMMAND_EXECUTION_FAILED"

  @UUID-de103e93-0f0f-4bfb-a05a-ba3a82d113fe @ECS-4846 @ECS-5863 @ECS-15356
  Scenario: 03 - Decrypt multiple card PINs
    Given a request is made to generate 4 PIN blocks for a card
    When a request is made to decrypt the PIN blocks
    Then the request to the security-commands endpoint to request a Pin Decryption returns an http status code of 200
    Then the response contains the clear PIN value
    And the response only contains decrypt pin fields
