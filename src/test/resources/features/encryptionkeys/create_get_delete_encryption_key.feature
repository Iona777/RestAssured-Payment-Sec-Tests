@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @CREATE_GET_DELETE_ENCRYPTION_KEY


Feature:
  As the Light authorisation service
  I want to be able to request the creation, retrieval and deletion of an encryption key

  @UUID-f626080d-5903-468a-8c5a-3a9b1f1edd0d @ECS-4825-AC1 @ECS-15883
  Scenario Outline: 01 - Create an encryption key
    Given an existing HSM managed with "<algorithm>" algorithm and "<keyType>" encryption key
    When the key is created on the Payment Security service with status "<status>"
    Then the request to create a Key Encryption returns an http status code of 201
    And the key is stored in the database without the variant value field

    Examples:
      | status   | keyType | algorithm |
      | ACTIVE   | ZPK     | 2DES      |
      | ACTIVE   | ZEK     | 3DES      |
      | ACTIVE   | CVK     | 2DES      |
      | INACTIVE | ZPK     | 2DES      |
      | INACTIVE | ZEK     | 3DES      |
      | INACTIVE | ZEK     | 2DES      |
      | INACTIVE | CVK     | 2DES      |

  @UUID-a7ad0f51-9695-4d83-91ab-4367e9bcbaa1 @ECS-4825-AC2 @ECS-15883
  Scenario Outline: 02 - Create an encryption key with a specific scope
    Given an existing HSM managed with "3DES" algorithm and "ZEK" encryption key
    When the key is created on the Payment Security service in "<zone>" zone, "<group>" group and "<bin>" bin
    Then the request to create a Key Encryption returns an http status code of 201
    Then the key is stored in the database with the expected scope
    And the key is stored in the database without the variant value field

    Examples:
      | zone    | group     | bin    |
      | PACASSO | Card Keys | 665542 |
      | PACASSO | Card Keys | none   |

  @UUID-14643b91-07fd-4f4a-a1ba-a980188d0e3a @ECS-5846 @ECS-15883
  Scenario Outline: 03 - Create an encryption key with a specific scope fails due to missing required field
    Given an existing HSM managed with "3DES" algorithm and "ZEK" encryption key
    When the key is created on the Payment Security service in "<zone>" zone, "<group>" group and "<bin>" bin
    Then the request to create a Key Encryption returns an http status code of 400
    And an error message "<errorMessage>" stating Missing required field is displayed
    Examples:
      | zone    | group     | bin    | errorMessage                             |
      | PACASSO | none      | 665542 | Group cannot be null                     |
      | none    | none      | 665542 | Zone cannot be null Group cannot be null |
      | none    | Card Keys | none   | Zone cannot be null                      |
      | PACASSO | none      | none   | Group cannot be null                     |

  @UUID-e44ccb71-7ed6-42cb-9402-87ece23eb2f5 @ECS-5294-AC1 @ECS-5993 @ECS-15883
  Scenario: 04 - Retrieve an encryption key value
    Given a key was created on the Payment Security service
    When the key is retrieved by its unique id and the embed query parameter is encrypted_key_value
    Then the request to retrieve a Key Encryption returns an http status code of 200
    And the key is stored in the database with the expected variant value field

  @UUID-b9f24d63-6baf-408e-812d-a0075c62875f @ECS-5295-AC1 @ECS-15883
  Scenario: 05 - Delete an existing encryption key
    Given a key was created on the Payment Security service
    When the key is deleted
    Then the request to delete a Key Encryption returns an http status code of 204
    And the key is not retrievable with its unique id
    Then the request to retrieve a Key Encryption returns an http status code of 404

  @UUID-77c96105-6289-48fe-b690-93362af96cd1 @ECS-4825-AC1 @ECS-4850-AC1 @ECS-15883
  Scenario Outline: 06 - Generate a Zone Encryption Key
    Given an HSM managed exists with "ZMK" encryption key
    When an "<algorithm>" "ZEK" encryption key is created identifying the ZMK as the management key and with no key value
    Then the request to create a Key Encryption returns an http status code of 201
    Then the new encryption key is created in the database with a new generated key value for only the keyblock encoding and check value

    Examples:
      | algorithm |
      | 2DES      |
      | 3DES      |

  @UUID-7e41e677-514a-48be-800e-8b1c728c9835 @ECS-4825-AC1 @ECS-4850-AC2 @ECS-15883
  Scenario Outline: 07 - Generate a Zone Encryption Key and retrieve the value
    Given an HSM managed exists with "ZMK" encryption key
    When an "<algorithm>" "ZEK" encryption key is created identifying the ZMK as the management key and with no key value  and with the show_value query parameter set to true
    Then the request to create a Key Encryption returns an http status code of 201
    Then the new encryption key is created in the database with a new generated key value for only the keyblock encoding and check value

    Examples:
      | algorithm |
      | 2DES      |
      | 3DES      |

  @UUID-b574eb30-9493-44f5-aa5b-efe5c757d948 @ECS-4825-AC1 @ECS-4850-AC3 @ECS-15883
  Scenario Outline: 08 - Attempt to generate a non-ZEK
    Given an HSM managed exists with "ZMK" encryption key
    When an "<algorithm>" "<type>" encryption key is created identifying the ZMK as the management key and with no key value
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "Generation of encryption keys for type <type> is not yet supported" and the error code is "ENCRYPTION_KEY_GENERATION_NOT_SUPPORTED"

    Examples:
      | algorithm | type |
      | 2DES      | ZMK  |
      | 3DES      | ZPK  |

  @UUID-38b9e96f-85db-4af7-b020-4e28774ec854 @ECS-4825-AC1 @ECS-4850-AC5 @ECS-15883
  Scenario Outline: 09 - Attempt to generate a ZEK with unknown management key
    Given an HSM managed exists with "ZMK" encryption key
    And an "<algorithm>" "ZEK" encryption key is created identifying an unknown ZMK as the management key
    Then the request to create a Key Encryption returns an http status code of 404
    And the encryption key unknown ZMK error message contains "No encryption key found for id " and the error code is "ENCRYPTION_KEY_NOT_FOUND"

    Examples:
      | algorithm |
      | 2DES      |
      | 3DES      |

  @UUID-d70128e0-7ad6-4951-97cb-01499751adee @ECS-4825-AC1 @ECS-4850-AC6 @ECS-15883
  Scenario Outline: 10 - Attempt to generate a ZEK with invalid management key
    Given an HSM managed exists with "ZMK" encryption key
    When an "<algorithm>" "ZEK" encryption key is created identifying an invalid ZMK as the management key
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "For generation of encryption keys, the management key must be a valid ZMK" and the error code is "MISSING_OR_INVALID_MANAGEMENT_KEY"

    Examples:
      | algorithm |
      | 2DES      |
      | 3DES      |

 # Management key for ZEKs tests

  @UUID-285cc469-2ad4-4242-8ccf-e4be50144b3d @ECS-6667 @ECS-15883
  Scenario Outline: 11 - Create ZEK with values correctly encrypted under the management key and management key is referenced
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database
    When a valid request that contains the "<valueType>" value encrypted under the "referenced" management ZMK key is made to create a new "ZEK" encryption key
    Then the request to create a Key Encryption returns an http status code of 201
    And the "<valueType>" value should be encrypted under the given ZMK
    And ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" should be linked as the management key

    Examples:
      | valueType           |
      | Variant              |
      | Keyblock             |
      | Variant and Keyblock |

  @UUID-7ddcade6-0880-433f-bed9-851483d712f5 @ECS-6667 @ECS-15883
  Scenario Outline: 12 - Create ZEK with values not encrypted under the management key and management key is referenced
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database
    When a valid request that contains the "<valueType>" value not encrypted under the "referenced" management ZMK key is made to create a new ZEK encryption key
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "ZEK not encrypted by referenced management key (ZMK)" and the error code is "BAD_REQUEST"

    Examples:
      | valueType           |
      | Variant              |
      | Keyblock             |
      | Variant and Keyblock |

  @UUID-80d743ff-5c66-41c5-aa92-f82f766c9ac0 @ECS-6667 @ECS-15883
  Scenario Outline: 13 - Create ZEK with values correctly encrypted under the management key and no management key is referenced
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database as default management key
    When a valid request that contains the "<valueType>" value encrypted under the "unreferenced" management ZMK key is made to create a new "ZEK" encryption key
    Then the request to create a Key Encryption returns an http status code of 201
    And the "<valueType>" value should be encrypted under the given ZMK
    And ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" should be linked as the management key

    Examples:
      | valueType           |
      | Variant              |
      | Keyblock             |
      | Variant and Keyblock |

  @UUID-d4f48ac6-1137-4148-8592-629ff509ea3a @ECS-6667 @ECS-15883
  Scenario Outline: 14 - Create ZEK with values not encrypted under the management key and no management key is referenced
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database
    When a valid request that contains the "<valueType>" value not encrypted under the "unreferenced" management ZMK key is made to create a new ZEK encryption key
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "Invalid ZEK key value(s). Cannot export to storage ZMK" and the error code is "BAD_REQUEST"

    Examples:
      | valueType           |
      | Variant              |
      | Keyblock             |
      | Variant and Keyblock |

  @UUID-757fb8d5-a424-4c83-8895-6049458129f8 @ECS-6667 @ECS-15883
  Scenario Outline: 15 - Create ZEK with values correctly encrypted under the management key and management key is referenced, when multiple ZMKs exist
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database as default management key
    And ZMK 'aaf1ee6b-216c-4d7b-aebb-45fa8685174a' exists in the database
    When a valid request that contains "Keyblock" value encrypted under the referenced management "<zmkId>" ZMK key is made to create a new ZEK encryption key
    Then the request to create a Key Encryption returns an http status code of 201
    And the "Keyblock" value should be encrypted under the given ZMK
    And ZMK "<zmkId>" should be linked as the management key

    Examples:
      | zmkId                               |
      | c3be4b32-fe86-473d-9166-da1d77a4a96b |
      | aaf1ee6b-216c-4d7b-aebb-45fa8685174a |

  @UUID-3b83a167-d0b2-4fff-a3dd-e28616a0df47 @ECS-6667 @ECS-15883
  Scenario Outline: 16 - Create ZEK with no values but referenced management ZMK key, when multiple ZMKs exist
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database as default management key
    And ZMK "aaf1ee6b-216c-4d7b-aebb-45fa8685174a" exists in the database
    When a valid request that contains "no" value encrypted under the referenced management "<zmkId>" ZMK key is made to create a new ZEK encryption key
    Then the request to create a Key Encryption returns an http status code of 201
    And the "Keyblock" value should be encrypted under the given ZMK
    And ZMK "<zmkId>" should be linked as the management key

    Examples:
      | zmkId                               |
      | c3be4b32-fe86-473d-9166-da1d77a4a96b |
      | aaf1ee6b-216c-4d7b-aebb-45fa8685174a |

  @UUID-84541aa1-11ff-43c2-bb50-b2b27b5976ba @ECS-6667 @ECS-15883
  Scenario: 17 - Create ZEK with no values and no referenced management key
    Given ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" exists in the database as default management key
    When a valid request that contains no values and no referenced management ZMK key is made to create a new ZEK encryption key
    Then the request to create a Key Encryption returns an http status code of 201
    And the "Keyblock" value should be encrypted under the given ZMK
    And ZMK "c3be4b32-fe86-473d-9166-da1d77a4a96b" should be linked as the management key

  # Check Value tests

  @UUID-acb824df-4a27-4c60-aedd-8432846e50d6 @ECS-6642 @ECS-15883
  Scenario Outline: 18 - Create encryption key with correct values and Check Value provided
    Given key "c3be4b32-fe86-473d-9166-da1d77a4a96b" has Check Value "62D017"
    When  a valid request that contains the "<valueType>" value encrypted under the above key and Check Value "<check_value>" is made to create a new "<key_type>" encryption key
    Then the request to create a Key Encryption returns an http status code of 201

    Examples:
      | valueType           | key_type | check_value |
      | Variant              | ZEK      | 62D017      |
      | Variant              | ZMK      | 0A5687      |
      | Variant              | ZPK      | 8E190F      |
      | Variant              | CVK      | 870376      |
      | Keyblock             | ZEK      | 62D017      |
      | Keyblock             | ZMK      | 0A5687      |
      | Keyblock             | ZPK      | 8E190F      |
      | Keyblock             | CVK      | 870376      |
      | Variant and Keyblock | ZEK      | 62D017      |
      | Variant and Keyblock | ZMK      | 0A5687      |
      | Variant and Keyblock | ZPK      | 8E190F      |
      | Variant and Keyblock | CVK      | 870376      |

  @UUID-d14d8677-8dc4-440e-8b58-7941e5781d45 @ECS-6642 @ECS-15883
  Scenario Outline: 19 - Create encryption key with correct values but incorrect Check Value provided
    Given key "c3be4b32-fe86-473d-9166-da1d77a4a96b" has Check Value "62D017"
    When  a valid request that contains the "<valueType>" value encrypted under the above key and Check Value "FFFFFF" is made to create a new "<keyType>" encryption key
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "Invalid Key Check Value" and the error code is "BAD_REQUEST"

    Examples:
      | valueType           | keyType |
      | Variant              | ZEK      |
      | Variant              | ZMK      |
      | Variant              | ZPK      |
      | Variant              | CVK      |
      | Keyblock             | ZEK      |
      | Keyblock             | ZMK      |
      | Keyblock             | ZPK      |
      | Keyblock             | CVK      |
      | Variant and Keyblock | ZEK      |
      | Variant and Keyblock | ZMK      |
      | Variant and Keyblock | ZPK      |
      | Variant and Keyblock | CVK      |

  @UUID-ce02d7bd-e6e8-4baa-93d4-98c31bb1e716 @ECS-6642 @ECS-15883
  Scenario: 20 - Create encryption key with both Variant and Keyblock values, and Key Check Value that doesn’t match at least one
    Given key "c3be4b32-fe86-473d-9166-da1d77a4a96b" has Check Value "62D017"
    And key "aaf1ee6b-216c-4d7b-aebb-45fa8685174a" has Check Value "6087FC"
    When a valid request is made to create a new encryption key
    And the "Variant" value for key "c3be4b32-fe86-473d-9166-da1d77a4a96b" is provided
    And the "Keyblock" value for key "aaf1ee6b-216c-4d7b-aebb-45fa8685174a" is provided
    And the "62D017" Check Value is provided
    And the post encryption key request is sent
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "Invalid ZEK key value(s). Cannot export to storage ZMK" and the error code is "BAD_REQUEST"

  @UUID-12b003ed-f8aa-40fa-9b76-f8b52ede77bf @ECS-6642 @ECS-15883
  Scenario Outline: 21 - Create encryption key with either Variant or Keyblock value, and no Key Check Value provided
    Given key "c3be4b32-fe86-473d-9166-da1d77a4a96b" has Check Value "62D017"
    When a valid request is made to create a new encryption key
    And  the "<valueType>" value for key "c3be4b32-fe86-473d-9166-da1d77a4a96b" is provided
    And no Check Value is provided
    And the post encryption key request is sent
    Then the request to create a Key Encryption returns an http status code of 400
    And the encryption key error message contains "<expectedErrorMessage>" and the error code is "BAD_REQUEST"

    Examples:
      | valueType            | expectedErrorMessage                                      |
      | Variant              | Check value cannot be null when variant value is present  |
      | Keyblock             | Check value cannot be null when keyblock value is present |
      | Variant and Keyblock | Check value cannot be null when variant value is present  |
