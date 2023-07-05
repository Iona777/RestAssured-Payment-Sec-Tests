@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @ENCRYPT_DECRYPT_PAN
Feature: Encrypt and decrypt a card PAN

  @UUID-1759eeb1-955a-41e5-b471-2951bc59f967 @ECS-4844
  Scenario: Encrypt and decrypt a card PAN
    When a request is made to encrypt a card PAN
    Then a successful data encryption response is received
    And the returned encrypted data is as expected
    When a request is made to decrypt the data item
    Then a successful data decryption response is received
    And the returned decrypted data is as expected

  @UUID-71831bcd-274a-49e8-9d62-712654e74d60 @ECS-4844
  Scenario: Encrypt and decrypt multiple card PANs
    When a request is made to encrypt multiple card PANs
    Then a successful data encryption response is received
    And the returned encrypted data is as expected and in the correct order
    When a request is made to decrypt multiple data items
    Then a successful data decryption response is received
    And the returned decrypted data is as expected and in the correct order

  @UUID-7030aa05-365a-460b-8972-8e3d578fe2c5 @ECS-6579
  Scenario: Encrypt and decrypt max data size
    When a request is made to encrypt the maximum allowed length data
    Then a successful data encryption response is received
    And the returned encrypted data is as expected
    When a request is made to decrypt the data item
    Then a successful data decryption response is received
    And the returned decrypted data is as expected

  @9d2233b2-a2ad-466e-8c55-0f78b6906644 @ECS-6579
  Scenario: Encryption fails when data exceeds max data size
    When a request is made to encrypt data exceeding the maximum allowed length
    Then a failed data encryption response is received as bad request
    And the failed data encryption response has error code BAD_REQUEST
