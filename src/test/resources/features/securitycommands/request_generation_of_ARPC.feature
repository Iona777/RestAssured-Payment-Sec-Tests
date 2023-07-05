@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @GENERATE_ARPC

Feature: Request to generate the ARPC for occasions where the originally generated ARPC  is no longer correct
  As the Light Auth Service
  I want to be able to request the generation of an ARPC via the Payment Security Service
  So that it can be returned back to the Card for validation of the response

  @UUID-9b331972-1f9e-470a-bbad-d2ba1bfd4454 @ECS-13571 @ECS-14245
  Scenario: 01 - Generate ARPC
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 200
    And the generate ARPC response contains the same values that are in the request
    And the arpc in the response is "F6F2DEB647B43663"
    And the response only contains generate arpc fields

  @UUID-a1ec65f6-a514-4c95-83f3-c386e569e21c @ECS-13571
  Scenario Outline: 02 - Referenced AC Key not found
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to "<acKey>" which does NOT exist
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 412
    And the ARPC generation error message contains "Encryption key does not exist with id <acKey>" and the error code is "ENCRYPTION_KEY_NOT_FOUND"

    Examples:
      | acKey                                |
      #Not on DB
      | 99999999-9999-9999-9999-999999999999 |
      #On DB, but Type is not AC, so it is not an AC key
      | 67c17efb-c88a-4b4e-96d7-ba59931ce79a |

  @UUID-6b7b72b7-44ae-4a75-93d7-665a294621df @ECS-13571
  Scenario Outline: 03 - Bad data sent to security-commands endpoint - invalid or missing PAN
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC PAN is set to "<Invalid PAN>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "Pan should be between 12 and 19 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid PAN           |
      #Too short
      | 12345678901          |
      #Too long
      | 12345678901234567890 |
      #Missing mandatory field
      |                      |

  @UUID-6c960c76-4019-47d1-b4ea-bb19ddb6225e @ECS-13571
  Scenario Outline: 04 - Bad data sent to security-commands endpoint - invalid PAN sequence number
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC PAN sequence number is set to "<Invalid Sequence No>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "If provided, PAN sequence number must be 2 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid Sequence No |
      #Too short
      | 1                   |
      #Too long
      | 123                 |

  @UUID-f623799d-b51c-4afe-a288-cf12a9ffe63d @ECS-13571
  Scenario Outline: 05 - Bad data sent to security-commands endpoint - invalid or missing scheme
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC scheme is set to "<InvalidScheme>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "Invalid Scheme" and the error code is "BAD_REQUEST"

    Examples:
      | InvalidScheme |
      #Not valid value
      | Nonsense     |
      #Missing mandatory field
      |               |

  @UUID-be0093ce-f3d5-4f0c-b211-36d67f1568dd @ECS-13571
  Scenario Outline: 06 - Bad data sent to security-commands endpoint - invalid or missing method
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC method is set to "<InvalidMethod>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "Invalid Method" and the error code is "BAD_REQUEST"

    Examples:
      | InvalidMethod |
      #Not valid enum value
      | Nonsense     |
      #Missing mandatory field
      |               |

  @UUID-9dc2d6a9-87fc-4dd3-82c6-262ca0bded01 @ECS-13571
  Scenario Outline: 07 - Bad data sent to security-commands endpoint - invalid or missing arqc
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC arqc field is set to "<Invalid ARQC>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "The ARQC should be 16 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid ARQC      |
      #Too short
      | 123456789012345   |
      #Too long
      | 12345678901234567 |
      #Missing mandatory field
      |                   |

  @UUID-103cbd9a-ffdb-4ad1-b2b4-8de1e621bd7a @ECS-13571
  Scenario Outline: 08 - Bad data sent to security-commands endpoint - invalid or missing atc
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC atc field is set to "<Invalid ATC>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "The Application Transaction Counter should be 4 characters long" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid ATC |
      #Too short
      | 123         |
      #Too long
      | 12345       |
      #Missing mandatory field
      |             |

  @UUID-4ddbf411-2565-4401-b2ff-d6704e9a220f @ECS-13571
  Scenario Outline: 09 - Bad data sent to security-commands endpoint - invalid or missing csu
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    And the generate ARPC csu field is set to "<Invalid CSU>"
    When a POST request is made to the security-commands endpoint to request an ARPC generation
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 400
    And the ARPC generation error message contains "Allowed values for the length of field 'csu' are [4, 6, 8]" and the error code is "BAD_REQUEST"

    Examples:
      | Invalid CSU |
      #Wrong length
      | 123         |
      | 12345       |
      | 1234567     |
      | 123456789   |
      #Missing mandatory field
      |             |

  @UUID-9b6aaf58-c99a-43a0-869c-db34c85a19ee @ECS-13571
  Scenario: 10 - Requester does not have permission to generate ARPCs
    Given the payload is set to a default valid security commands payload to request an ARPC generation
    And the ARPC application cryptogram key is set to a valid value which exists
    When a POST request is made to the security-commands endpoint to request an ARPC generation without permission
    Then the request to the security-commands endpoint to request an ARPC generation returns an http status code of 403

