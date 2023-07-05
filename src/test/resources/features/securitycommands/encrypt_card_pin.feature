@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @ENCRYPT_PIN
Feature: Encrypt a card PIN
  As the Light authorisation service
  I want to be able to request the encryption of a card PIN

  @UUID-46f27ecc-a4b5-4723-84af-b9dd58818a2e @ECS-13572
  Scenario: 01 - Encrypt multiple card PINs
    Given a request is made to encrypt multiple card PINs
    Then a successful 'Ok' response is received with 'each of the encrypted PIN values'

  @UUID-556714f0-5cae-4f80-87b9-44a41078ca93 @ECS-13572
  Scenario: 02 - Encrypt a card PIN with a specific ZPK and the default pin block format
    Given a request is made to encrypt a card PIN 'with specific ZPK and default pin block format'
    Then a successful 'Ok' response is received with 'the encrypted PIN value plus zone values'

  @UUID-d1f0b605-3d73-4850-bc4c-f91d586cda63 @ECS-13572
  Scenario: 03 - Encrypt a card PIN with specific ZPK and an alternative pin block format
    Given a request is made to encrypt a card PIN 'with specific ZPK and alternative pin block format'
    Then a successful 'Ok' response is received with 'the encrypted PIN value plus zone values'

  @UUID-37d8956e-c05f-4877-b845-bc3ebc236029 @ECS-13572
  Scenario: 04 - Encrypt a card PIN with specific ZPK only
    Given a request is made to encrypt a card PIN 'with specific ZPK only'
    Then a successful 'Ok' response is received with 'the encrypted PIN value plus zone values'

  @UUID-ce95a014-05ec-474f-846b-e8e395d1015d @ECS-13572
  Scenario: 05 - Attempt with missing card pan
    Given a request is made to encrypt a card PIN 'but the card pan is missing'
    Then a failed response for 'missing card pan' is received with response code 400 and error code 'BAD_REQUEST'

  @UUID-55ff95a0-3488-40f0-8771-33faa0183e05 @ECS-13572
  Scenario: 06 - Attempt with non-existing ZPK
    Given a request is made to encrypt a card PIN 'with a non-existent ZPK'
    Then a failed response for 'non-existent ZPK' is received with response code 412 and error code 'ENCRYPTION_KEY_NOT_FOUND'

  @UUID-239a1671-2e0b-4ebb-adb4-82bf97e230ce @ECS-13572
  Scenario: 07 - Attempt with no authorisation details setup
    Given a valid request is made to encrypt a card PIN that has no authorisation details
    Then a failed response for 'authorisation' is received with response code 401 and error code 'UNAUTHORISED'
