@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @VERIFY_ARQC

Feature: Support ARQC validation with ARPC generation
  As a Product Owner
  I want the existing security-commands endpoint to validate an incoming ARQC from the Light Auth Service and generate an ARPC at the same time
  So that the Light Auth Service can verify that the Auth request has come from a genuine Card, and the ARPC can be returned to the card

  @UUID-0239abdb-a3c6-4942-ac54-044572608f68 @ECS14012 @ECS-14246
  Scenario: 01 - Validate EMV_OPTION_A_CKD_CSK ARQC with DPAS_METHOD_1 ARPC Generation
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "OK"
    And the response only contains verify arqc fields

  @UUID-872f4014-0d68-49ee-98e0-3a26fc97ed8d @ECS14012 @ECS-14246
  Scenario: 02 - Validate EMV_OPTION_A_CKD_CSK ARQC with ARPC Generation type not provided
    Given the payload is set to a default valid security commands payload to request an ARQC verification
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And there is no arpc in the verify ARQC response
    And the verify ARQC result is returned as "OK"
    And the response only contains verify arqc fields

  @UUID-9fdf7562-68d0-4ec4-8f45-f7aec223ec74 @ECS-14246
  Scenario: 03 - Validate EMV_OPTION_A_CKD_CSK ARQC with NOT_REQUIRED ARPC Generation
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC generation type is set to "NOT_REQUIRED"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And there is no arpc in the verify ARQC response
    And the verify ARQC result is returned as "OK"
    And the response only contains verify arqc fields

  @UUID-452fcb8b-96f0-421f-9b8f-1b0057cad487 @ECS14012 @ECS-14246
  Scenario: 04 - No CSU provided for DPAS_METHOD_1 ARPC Generation
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation with no CSU attribute
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400

  @UUID- @ECS14012 @ECS-14246
  Scenario: 05 - Blank CSU provided for DPAS_METHOD_1 ARPC Generation
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the verify ARPC csu field is set to ""
    And the ARQC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 400

  @UUID-42fa859a-63f6-45f4-b502-fe2935a50893 @ECS-14246
  Scenario: 06 - Verify ARQC with non-matching PAN
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN is set to length 19
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-0ef7a538-5ed9-4bd1-9c91-de3d51e21976 @ECS-14246
  Scenario: 07 - Verify ARQC with non-matching PAN Sequence No
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC PAN sequence number is set to "99"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-16b9fb12-7b11-4935-9b32-f7eef06f7d48 @ECS-14246
  Scenario: 08 - Verify ARQC with non-matching ATC
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC atc field is set to "1234"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-6fae8d4a-149e-40a9-8e56-a5fc113c91f0 @ECS-14246
  Scenario: 09 - Verify ARQC with non-matching Transaction Data
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC transaction data field is set to length 2
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields

  @UUID-c6cdb49e-7916-48ba-bcf1-116403bfa736 @ECS-14246
  Scenario: 10 - Verify ARQC with non-matching ARQC
    Given the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation
    And the ARQC application cryptogram key is set to a valid value which exists
    And the verify ARQC arqc field is set to "1234567890123456"
    When a POST request is made to the security-commands endpoint to request an ARQC verification
    Then the request to the security-commands endpoint to request an ARQC verification returns an http status code of 200
    And the verify ARQC response contains the same values that are in the request
    And the verify ARQC result is returned as "VERIFICATION_FAILED"
    And the response only contains verify arqc fields
