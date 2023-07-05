@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @MIGRATE_KEYS_VARIANT_TO_KEY_BLOCK

Feature:
  As the Light authorisation service
  I want to be able to request the migratation of keys from variant to key block


  @UUID-da18b8ea-fa53-4cbf-bdb6-63cb5f6ea3eb @ECS-6904 @ECS-155887
  Scenario Outline: 1. Migrate Variant ZMK, ZPK, CVK to Keyblock on insert
    Given a key with Zone A, Group B, and Code "PanKey" does not exist
    When a key is created with Zone "A", Group "B", Code "PanKey" and type "<keyType>"
    And a POST request is made to the encryption-keys endpoint with a variant value as the key
    Then the request to the encryption-keys endpoint to request a Key Encryption returns an http status code of 201
    And the key should be successfully created with code "PanKey"
    And Keyblock value for the key should have been generated through migration

    Examples:
      | keyType |
      | ZMK     |
      | ZPK     |
      | CVK     |

  @UUID-5254ce79-8872-46bc-b30a-a0280f66997f @ECS-6904 @ECS-155887
  Scenario: 2. Migrate Variant ZEK to Keyblock on insert when Keyblock version of the management key exists
    Given a key with Zone A, Group B, and Code "PanKey" does not exist
    And create a ZMK with both Variant and Keyblock values if one does not exist already
    When a key is created with Zone "A", Group "B", Code "PanKey" and type "ZEK"
    And the key value is under the variant management ZMK key
    And a POST request is made to the encryption-keys endpoint with a variant value as the key
    Then the request to the encryption-keys endpoint to request a Key Encryption returns an http status code of 201
    And the key should be successfully created with code "PanKey"
    And Keyblock value for the key should have been generated through migration using the existing ZMK
