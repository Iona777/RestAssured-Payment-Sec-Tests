@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @VERIFY_ARQC

Feature: Support ARQC validation
  As a Product Owner
  I want the existing security-commands endpoint to validate an incoming ARQC from the Light Auth Service
  So that the Light Auth Service can verify that the Auth request has come from a genuine Card

  @UUID-17f7f33d-062c-471e-bbf3-e6287d0654f1 @ECS-13570
  Scenario: 01 - Verify ARQC with valid EMV_OPTION_A_CKD_CSK ARQC
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "OK"
    And the response only contains verify arqc fields

  @UUID-3af59db7-01ae-49ae-8cd4-1356e47c6b69 @ECS-14068 @IGNORED
  Scenario: 02 - Verify ARQC with Non-matching PAN - minimum length
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN is set to length 14
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-8cd26183-d286-4e3a-ac2d-acf7d874c92e @ECS-14068
  Scenario: 03 - Verify ARQC with Non-matching PAN - maximum length
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN is set to length 19
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-328fd8ab-00dd-487a-8644-d0c6e0aa436c @ECS-14068
  Scenario: 04 - Verify ARQC with Non-matching PAN Sequence No
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN sequence number is set to "99"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-8a3b5fa8-049d-424e-b8e2-f090a474a59a @ECS-14068
  Scenario: 05 - Verify ARQC with Invalid ATC
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC atc field is set to "1234"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields


  @UUID-1b7f94e9-6a13-4800-9ecc-af4ba43e6f5d @ECS-14068
  Scenario: 06 - Verify ARQC with Invalid Transaction Data - minimum length
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC transaction data field is set to length 1
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-088d1cf2-23c9-4525-93be-6d5681ac91a7 @ECS-14068 @IGNORED
  Scenario: 07 - Verify ARQC with Invalid Transaction Data - maximum length
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC transaction data field is set to length 495
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-f3ac0612-c3fc-44e3-b5e5-65ff50c2b7ec @ECS-14068
  Scenario: 08 - Verify ARQC with Invalid ARQC
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC arqc field is set to "1234567890123456"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-90d2bc20-a2a9-43f9-b6a7-c4ff237df60b @ECS-13570
  Scenario Outline: 09 -  Referenced AC Key not found
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to "<acKey>" which does NOT exist
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 412
    And the ARPQ verification error message contains "Encryption key does not exist with id <acKey>" and the error code is "ENCRYPTION_KEY_NOT_FOUND"

    Examples:
      | acKey                                |
      #Not on DB
      | 99999999-9999-9999-9999-999999999999 |
      #On DB, but Type is not AC, so it is not an AC key
      | 67c17efb-c88a-4b4e-96d7-ba59931ce79a |

  @UUID-66d362d8-cae6-40b1-baaf-c51c9fa5e9e6 @ECS-13570
  Scenario Outline: 10 -  Bad data sent to security-commands endpoint - invalid PAN
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN is set to length <Invalid PAN Length>
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "Pan should be between 14 and 19 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid PAN Length |
      #Too short
      | 13                 |
      #Too long
      | 20                 |
      #Missing mandatory field
      | 0                  |

  @UUID-6bddd6b3-cd66-4f69-8c1e-905d930037bd @ECS-13570
  Scenario Outline: 11 -  Bad data sent to security-commands endpoint - invalid PAN sequence number
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN sequence number is set to "<Invalid Sequence No>"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "If provided, PAN Sequence Number must be 2 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid Sequence No |
      #Too short
      | 1                   |
      #Too long
      | 123                 |

  @UUID-ad2419b1-eda5-4d68-80a4-55e4b96f518f @ECS-13570
  Scenario Outline: 12 -  Bad data sent to security-commands endpoint - invalid or missing scheme
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC scheme is set to "<InvalidScheme>"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "Invalid Scheme" and the error code is "BAD_REQUEST"

    Examples:
      | InvalidScheme |
      #Not valid value
      | Nonsense     |
      #Missing mandatory field
      |               |

  @UUID-bcef4de9-ad30-4dab-a751-b28bd90adca4 @ECS-13570
  Scenario Outline: 13 -  Bad data sent to security-commands endpoint - invalid or missing arqc
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC arqc field is set to "<Invalid ARQC>"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "The ARQC should be 16 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid ARQC      |
      #Too short
      | 123456789012345   |
      #Too long
      | 12345678901234567 |
      #Missing mandatory field
      |                   |

  @UUID-aad9d2b4-c8ee-4e5e-b315-aef3cce2e19c @ECS-13570
  Scenario Outline: 14 -  Bad data sent to security-commands endpoint - invalid or missing atc
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC atc field is set to "<Invalid ATC>"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "The Application Transaction Counter should be 4 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid ATC |
      #Too short
      | 123         |
      #Too long
      | 12345       |
      #Missing mandatory field
      |             |


  @UUID-4215afcc-d438-4d91-a4f8-986e005f4b76 @ECS-13570
  Scenario Outline: 15 -  Bad data sent to security-commands endpoint - invalid length transaction data
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC transaction data field is set to length <Invalid Transaction Data Length>
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400
    And the ARPQ verification error message contains "<Error Message>" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid Transaction Data Length | Error Message                                                                   |
      | 496                             | The Application Transaction Data should not be greater than 495 characters long |
      | 0                               | The Application Transaction Data cannot be null or blank                        |

  @UUID-73dfa8de-b8d1-4f15-8cd2-6cc01a4e0c15 @ECS-13570
  Scenario: 16 - Requester does not have permission to verify ARQCs
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification without permission
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 403



