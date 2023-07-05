@COMPONENT_TEST @SERVICE_PLATFORM @PAYMENT_SECURITY @CARD @QUERY_ENCRYPTION_KEY

  Feature:
    As the Light authorisation service
    I want to be able to request the querying of encryption keys

    @UUID-b126b822-51d2-4fb3-924e-27f814fcffb7 @ECS-4828-AC1 @ECS-15890
    Scenario: 1. Retrieve a single encryption key by code
      Given two encryption keys are created with different codes
      When the encryption keys are queried for a specific code
      Then a successful OK response is received with the single encryption key with the specified code

    @UUID-317a2be7-9339-42e2-866a-57c72c0a6245 @ECS-5976-AC1 @ECS-4828-AC2 @ECS-15890
    Scenario: 2a. Retrieve the latest version of an encryption key by code ("latest_index_only") set
      Given two encryption keys are created with different codes and a specific index
      And two encryption keys are created with the same codes as before and a higher index
      When the encryption keys are queried for one of the codes with latest index
      Then a successful OK response is received with the single encryption key with the queried code and the higher index

    @UUID-fafa80f3-7e47-4ac0-9573-bfc5f41dc78c @ECS-4828-AC2 @ECS-5976-AC2 @ECS-15890
    Scenario: 2b. Retrieve the latest version of an encryption key by code ("latest_index_only") not set
      Given two encryption keys are created with different codes and a specific index
      And two encryption keys are created with the same codes as before and a higher index
      When the encryption keys are queried for one of the codes without latest index
      Then a successful OK response is received with both encryption key with the queried code and all indexes

    @UUID-d3552d69-4b06-4211-b5e1-997e59c68c51 @ECS-4828-AC3 @ECS-15890
    Scenario: 3. Retrieve a specific version of an encryption key by code
      Given two encryption keys are created with different codes and a specific index
      And two encryption keys are created with the same codes as before and a higher index
      When the encryption keys are queried for one of the codes and the smaller index
      Then a successful OK response is received with the single encryption key with the queried code and the smaller index

    @UUID-ebd994e5-2b2a-4ca4-ac48-3ffe50f7e332 @ECS-4828-AC4 @ECS-15890
    Scenario Outline: 4. Retrieve a single encryption key by code and scope
      Given two encryption keys with the same code and "<scope1>" and "<scope2>"
      When the encryption keys are queried for the code and the scope of "<query-scope>"
      Then a successful OK response is received with the single encryption key with the code and scope of "<scope1>"

      Examples:
        | scope1                 | scope2                 | query-scope            |
        | zone A, group A, bin A | zone B, group B, bin B | zone A, group A, bin A |
        | zone A, group A        | zone B, group B        | zone A, group A        |
        | zone A                 | zone B                 | zone A                 |
        | bin A                  | bin B                  | bin A                  |
        | group A                | group B                | group A                |
        | zone A, group A, bin A | zone A, group A, bin B | zone A, group A, bin A |
        | zone A, group A, bin A | zone B, group A, bin A | zone A, group A, bin A |
        | zone A, group A, bin A | zone B, group B, bin B | zone A, group A        |
        | zone A, group A, bin A | zone B, group B, bin B | zone A                 |

    @UUID-98948231-5953-4e8f-8ad2-13f963e65524 @ECS-4828-AC5 @ECS-15890
    Scenario: 5. Retrieve encryption keys by state - active only
      Given two encryption keys are created with different codes
      And two inactive encryption keys are created with different codes
      When the encryption keys are queried with status = "ACTIVE"
      Then a successful OK response is received only with active encryption keys

    @UUID-40b1ee66-d73f-4c38-9051-b0cf64af47cb- @ECS-4828-AC5 @ECS-15890
    Scenario: 5b. Retrieve encryption keys by state - inactive only
      Given two encryption keys are created with different codes
      And two inactive encryption keys are created with different codes
      When the encryption keys are queried with status = "INACTIVE"
      Then a successful OK response is received only with inactive encryption keys

    @UUID-e696edab-6a3e-4eca-9fae-6620ea7d65ec @ECS-4828-AC6 @ECS-5875 @ECS-15890
    Scenario: 6. Retrieve all encryption keys
      Given two encryption keys are created with different codes and a specific index
      And two encryption keys are created with the same codes as before and a higher index
      And an active encryption key with a different code and specific zone, group and bin
      And an inactive encryption key with a different code and group but the same zone
      When the encryption keys are queried without query parameters
      Then a successful OK response is received with all keys with highest index both active and inactive
      And a successful OK response is received and the keys are ordered ascending on creation date

    @UUID-6d691c2c-069a-4918-856b-97c72e649ba6 @ECS-4828-AC7 @ECS-5993 @ECS-15890
    Scenario: 7. Retrieve encryption keys with different attributes
      Given two encryption keys are created with different codes and a specific index
      And two encryption keys are created with the same codes as before and a higher index
      And an active encryption key with a different code and specific zone, group and bin
      And an inactive encryption key with a different code and group but the same zone
      When the encryption keys are queried with the embed query parameter set to encrypted_key_value
      Then a successful OK response is received with all keys with highest index both active and inactive

    @UUID-6d6f55cb-4f13-416e-a2ee-3a8cff42151a @ECS-5955 @ECS-15890
    Scenario: 8: Query encryption keys for multiple zones
      Given active keys are created with three different zones
      When a request is made to search for encryption keys for zone1 and zone2
      Then Keys for only zone1 and zone2 should be returned
