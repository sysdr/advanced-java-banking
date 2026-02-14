package com.bank.identity;

import java.time.LocalDate;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Banking Identity Model Demo ---");
        IdentityService identityService = new IdentityService();

        // 1. Create a personal Party
        String personPartyId = identityService.createParty("Alice Wonderland");
        System.out.println("\nCreated Person Party ID: " + personPartyId);

        // Add identifiers for Alice
        String alicePassportId = identityService.addIdentifier(personPartyId, IdentifierType.PASSPORT, "P1234567");
        identityService.addIdentifier(personPartyId, IdentifierType.NATIONAL_ID, "9876543210");
        identityService.addIdentifier(personPartyId, IdentifierType.EMAIL, "alice@example.com");

        // 2. Create a corporate Party
        String corpPartyId = identityService.createParty("Global Financial Corp");
        System.out.println("\nCreated Corporate Party ID: " + corpPartyId);

        // Add identifiers for Global Financial Corp
        identityService.addIdentifier(corpPartyId, IdentifierType.BUSINESS_REGISTRATION, "GC-2023-001");
        identityService.addIdentifier(corpPartyId, IdentifierType.TAX_ID, "TIN-99887766");

        // 3. Retrieve and display Alice's details
        System.out.println("\n--- Retrieving Alice's Details ---");
        Optional<IdentityService.PartyDetails> aliceDetails = identityService.getPartyDetails(personPartyId);
        aliceDetails.ifPresentOrElse(
            details -> System.out.println(details),
            () -> System.out.println("Alice's Party not found.")
        );

        // 4. Simulate a passport change: Deactivate old passport, add new one
        System.out.println("\n--- Simulating Passport Change for Alice ---");
        System.out.println("Deactivating old passport ID: " + alicePassportId);
        identityService.deactivateIdentifier(alicePassportId);
        String newAlicePassportId = identityService.addIdentifier(personPartyId, IdentifierType.PASSPORT, "P9876543");
        System.out.println("Added new passport ID: " + newAlicePassportId);

        // 5. Retrieve Alice's details again to see the change
        System.out.println("\n--- Retrieving Alice's Details After Passport Change ---");
        aliceDetails = identityService.getPartyDetails(personPartyId);
        aliceDetails.ifPresentOrElse(
            details -> System.out.println(details),
            () -> System.out.println("Alice's Party not found.")
        );

        // 6. Demonstrate error handling for non-existent party
        System.out.println("\n--- Attempting to add identifier to non-existent Party ---");
        try {
            identityService.addIdentifier("NON_EXISTENT_PARTY", IdentifierType.EMAIL, "test@test.com");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("\n--- Demo Complete ---");
    }
}
