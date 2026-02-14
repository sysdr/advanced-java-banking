package com.bank.identity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class IdentityServiceTest {

    private IdentityService identityService;

    @BeforeEach
    void setUp() {
        identityService = new IdentityService();
    }

    @Test
    void testCreateParty() {
        String partyId = identityService.createParty("Test Party");
        assertNotNull(partyId);
        assertTrue(partyId.length() > 0);

        Optional<IdentityService.PartyDetails> details = identityService.getPartyDetails(partyId);
        assertTrue(details.isPresent());
        assertEquals("Test Party", details.get().getParty().getName());
    }

    @Test
    void testAddIdentifier() {
        String partyId = identityService.createParty("Test Party With Identifiers");
        String identifierId1 = identityService.addIdentifier(partyId, IdentifierType.NATIONAL_ID, "ID123");
        String identifierId2 = identityService.addIdentifier(partyId, IdentifierType.PASSPORT, "PASSPORT456");

        assertNotNull(identifierId1);
        assertNotNull(identifierId2);

        Optional<IdentityService.PartyDetails> details = identityService.getPartyDetails(partyId);
        assertTrue(details.isPresent());
        assertEquals(2, details.get().getIdentifiers().size());

        List<Identifier> activeIdentifiers = details.get().getIdentifiers().stream()
                .filter(i -> i.getStatus() == IdentifierStatus.ACTIVE)
                .collect(Collectors.toList());
        assertEquals(2, activeIdentifiers.size());
        assertTrue(activeIdentifiers.stream().anyMatch(i -> i.getValue().equals("ID123")));
        assertTrue(activeIdentifiers.stream().anyMatch(i -> i.getValue().equals("PASSPORT456")));
    }

    @Test
    void testAddIdentifierToNonExistentPartyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            identityService.addIdentifier("NON_EXISTENT", IdentifierType.EMAIL, "test@example.com");
        });
    }

    @Test
    void testDeactivateIdentifier() {
        String partyId = identityService.createParty("Party for Deactivation Test");
        String activeIdentifierId = identityService.addIdentifier(partyId, IdentifierType.NATIONAL_ID, "ACTIVE_ID_1");

        // Verify it's active initially
        Optional<IdentityService.PartyDetails> detailsBefore = identityService.getPartyDetails(partyId);
        assertTrue(detailsBefore.isPresent());
        assertTrue(detailsBefore.get().getIdentifiers().stream()
                .filter(i -> i.getIdentifierId().equals(activeIdentifierId))
                .anyMatch(i -> i.getStatus() == IdentifierStatus.ACTIVE));

        // Deactivate
        boolean deactivated = identityService.deactivateIdentifier(activeIdentifierId);
        assertTrue(deactivated);

        // Verify it's inactive now
        Optional<IdentityService.PartyDetails> detailsAfter = identityService.getPartyDetails(partyId);
        assertTrue(detailsAfter.isPresent());
        assertTrue(detailsAfter.get().getIdentifiers().stream()
                .filter(i -> i.getIdentifierId().equals(activeIdentifierId))
                .anyMatch(i -> i.getStatus() == IdentifierStatus.INACTIVE));
        
        // Ensure no active identifier with the old ID exists
        assertFalse(detailsAfter.get().getIdentifiers().stream()
                .filter(i -> i.getIdentifierId().equals(activeIdentifierId))
                .anyMatch(i -> i.getStatus() == IdentifierStatus.ACTIVE));
    }

    @Test
    void testGetPartyDetailsNotFound() {
        Optional<IdentityService.PartyDetails> details = identityService.getPartyDetails("NON_EXISTENT_PARTY");
        assertFalse(details.isPresent());
    }

    @Test
    void testIdentifierImmutability() {
        String partyId = identityService.createParty("Immutable Test Party");
        Identifier identifier = new Identifier(partyId, IdentifierType.EMAIL, "immutable@test.com", LocalDate.now(), IdentifierStatus.ACTIVE);
        
        // Attempting to change a final field would be a compile-time error.
        // We're testing that the 'withStatus' method returns a NEW instance.
        Identifier modifiedIdentifier = identifier.withStatus(IdentifierStatus.INACTIVE);

        assertNotSame(identifier, modifiedIdentifier); // Should be different objects
        assertEquals(IdentifierStatus.ACTIVE, identifier.getStatus()); // Original is unchanged
        assertEquals(IdentifierStatus.INACTIVE, modifiedIdentifier.getStatus()); // New one has new status
        assertEquals(identifier.getIdentifierId(), modifiedIdentifier.getIdentifierId()); // ID remains the same
    }
}
