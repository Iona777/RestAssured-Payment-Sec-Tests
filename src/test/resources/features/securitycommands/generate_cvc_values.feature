@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @GENERATE_CVC
Feature: Generate CVV/CVC values
  As the Light authorisation service
  I want to be able to request the generation of CVV/CVC values

  @UUID-c0ad14af-93cc-4925-a740-3b49ef6dbf8a @ECS-4849 @ECS-15360
  Scenario Outline: 01 - Generate single CVC
    Given a card verification key exists for "<cvk>"
    And the pan and expiry date are provided as "<pan>" and "<expiry_date>"
    And the service code is provided as "<service_code>"
    When a request is made to generate a CVC of type "<cvcType>" using the card's verification key
    Then the request to the security-commands endpoint to request a Cvc Generation returns an http status code of 200
    And the response contains the generated CVC value of "<generated_cvc>"
    And the response only contains generate cvcs fields

    Examples:
      | cvk       | pan               | expiry_date | service_code | cvcType | generated_cvc |
      | chip      | 5272510156090480  | 2025-01-31  | 999          | CVC1    | 367           |
      #| magstripe | 70600095654703948 | 2022-11-30  | 123          | CVC1    | 676           |
      #| magstripe | 70600095654703948 | 2022-11-30  | 000          | CVC2    | 166           |


  @UUID-176d9853-95b9-4eeb-a237-30819f2b18cb @ECS-5861 @ECS-15360
  Scenario Outline: 02 - Support both variant and keyblock Key Types with HSM
    Given only VARIANT of a card verification key exists for "<cvk>"
    And the pan and expiry date are provided as "<pan>" and "<expiry_date>"
    And the service code is provided as "<service_code>"
    When a request is made to generate a CVC of type "<cvcType>" using the card's verification key
    Then the request to the security-commands endpoint to request a Cvc Generation returns an http status code of 200
    And the response contains the generated CVC value of "<generated_cvc>"
    And the response only contains generate cvcs fields

    Examples:
      | cvk       | pan               | expiry_date | service_code | cvcType | generated_cvc |
      | chip      | 70600095654703948 | 2022-11-30  | 999          | CVC1    | 690           |
      | magstripe | 70600095654703948 | 2022-11-30  | 123          | CVC1    | 676           |
      | magstripe | 70600095654703948 | 2022-11-30  | 000          | CVC2    | 166           |
