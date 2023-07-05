@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @TRANSLATE_PIN_BLOCK

Feature: Translate pin block to ZPK
  As the Light authorisation service
  I want to be able to request the translation of pin block to ZPK

  @UUID-d9fe0b98-2025-46ef-8a26-d7b021555767 @ECS-4848 @ECS-15894
  Scenario Outline: 01 - Translate a card PIN block to a zone
    Given a request is made to generate 1 PIN blocks for a card
    And 1 ZPK keys are created
    When a request is made to translate each of the card PIN block to the ZPK in "<pin-block-format>"
    Then the request to the security-commands endpoint to translate the card PIN block to the ZPK returns an http status code of 200
    And the translate pin to zones response contains the expected values
    And the response only contains translate pin to zones fields

    Examples:
      | pin-block-format  |
      | ISO_ANSI_FORMAT_0 |
      | ISO_FORMAT_1      |
      | ISO_ANSI_FORMAT_3 |

  @UUID-4bca7090-431f-43ae-93e4-beb8c668e64b @ECS-4848 @ECS-5863 @ECS-15894
  Scenario: 02 - Translate multiple card PIN blocks to a zone
    Given a request is made to generate 4 PIN blocks for a card
    And 1 ZPK keys are created
    When a request is made to translate each of the card PIN block to the ZPK in "ISO_ANSI_FORMAT_0"
    Then the request to the security-commands endpoint to translate the card PIN block to the ZPK returns an http status code of 200
    And the translate pin to zones response contains the expected values
    And the response only contains translate pin to zones fields

  @UUID-cc01b21f-2532-413c-97db-28fd3353d34b @ECS-4848 @ECS-15894
  Scenario: 03 - Translate a card PIN block to multiple zones
    Given a request is made to generate 1 PIN blocks for a card
    And 2 ZPK keys are created
    When a request is made to translate each of the card PIN block to the ZPK in "ISO_ANSI_FORMAT_0"
    Then the request to the security-commands endpoint to translate the card PIN block to the ZPK returns an http status code of 200
    And the translate pin to zones response contains the expected values
    And the response only contains translate pin to zones fields

  @UUID-23e71a55-ee7e-49bd-b534-1493a7ed7074 @ECS-6371 @ECS-15894
  Scenario: 04 - Translate PIN encrypted by Variant LMK
    Given a request is made to generate 1 PIN blocks for a card
    And a ZPK key does not have a keyblock value, but has a variant value
    When a request is made to translate each of the card PIN block to the ZPK in "ISO_ANSI_FORMAT_0"
    Then the request to the security-commands endpoint to translate the card PIN block to the ZPK returns an http status code of 412
    And the translate pin to zones error message contains "Encryption key does have a keyblock value that is needed for this operation" and the error code is "ENCRYPTION_KEY_NO_KEYBLOCK_VALUE"
