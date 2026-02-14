package com.bank.identity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IdentityService {
    // In-memory "storage" for Parties and Identifiers
    private final Map<String, Party> partyStore = new ConcurrentHashMap<>();
    private final Map<String, List<Identifier>> identifierStore = new ConcurrentHashMap<>(); // PartyId -> List of its Identifiers

    /**
     * Creates a new Party in the system.
     * @param name The name of the party (e.g., "John Doe", "Acme Corp").
     * @return The unique ID of the newly created Party.
     */
    public String createParty(String name) {
        Party party = new Party(name);
        partyStore.put(party.getPartyId(), party);
        identifierStore.put(party.getPartyId(), new ArrayList<>()); // Initialize identifier list for the party
        System.out.println("[SERVICE] Created Party: " + party);
        return party.getPartyId();
    }

    /**
     * Adds a new identifier to an existing Party.
     * This creates a new immutable Identifier record.
     * @param partyId The ID of the Party to which this identifier belongs.
     * @param type The type of the identifier (e.g., PASSPORT, NATIONAL_ID).
     * @param value The actual value of the identifier (e.g., "AB1234567").
     * @return The unique ID of the newly added Identifier.
     * @throws IllegalArgumentException if the partyId does not exist.
     */
    public String addIdentifier(String partyId, IdentifierType type, String value) {
        if (!partyStore.containsKey(partyId)) {
            throw new IllegalArgumentException("Party with ID " + partyId + " not found.");
        }

        Identifier newIdentifier = new Identifier(partyId, type, value, LocalDate.now(), IdentifierStatus.ACTIVE);
        identifierStore.get(partyId).add(newIdentifier);
        System.out.println("[SERVICE] Added Identifier: " + newIdentifier + " to Party: " + partyId);
        return newIdentifier.getIdentifierId();
    }

    /**
     * Deactivates an existing identifier.
     * This simulates versioning by creating a new immutable Identifier record with INACTIVE status.
     * In a real system, you might retrieve the old one, mark it inactive, and then add a new one if it's an "update".
     * For this demo, we'll just deactivate an existing record.
     * @param identifierId The ID of the identifier to deactivate.
     * @return true if the identifier was found and deactivated, false otherwise.
     */
    public boolean deactivateIdentifier(String identifierId) {
        for (List<Identifier> identifiers : identifierStore.values()) {
            Optional<Identifier> found = identifiers.stream()
                                                    .filter(i -> i.getIdentifierId().equals(identifierId) && i.getStatus() == IdentifierStatus.ACTIVE)
                                                    .findFirst();
            if (found.isPresent()) {
                Identifier oldIdentifier = found.get();
                // Create a new immutable identifier with INACTIVE status
                Identifier deactivatedIdentifier = oldIdentifier.withStatus(IdentifierStatus.INACTIVE);
                
                // Replace the old identifier with the new, deactivated one in the list
                // This is a simplification for in-memory, real systems would append or manage versions explicitly.
                identifiers.remove(oldIdentifier);
                identifiers.add(deactivatedIdentifier);
                System.out.println("[SERVICE] Deactivated Identifier: " + deactivatedIdentifier);
                return true;
            }
        }
        System.out.println("[SERVICE] Identifier with ID " + identifierId + " not found or already inactive.");
        return false;
    }


    /**
     * Retrieves a Party by its ID, along with all its associated identifiers (active and inactive).
     * @param partyId The ID of the Party to retrieve.
     * @return An Optional containing the Party and its identifiers, or empty if not found.
     */
    public Optional<PartyDetails> getPartyDetails(String partyId) {
        Party party = partyStore.get(partyId);
        if (party == null) {
            return Optional.empty();
        }
        List<Identifier> identifiers = identifierStore.getOrDefault(partyId, Collections.emptyList());
        System.out.println("[SERVICE] Retrieved Party Details for: " + partyId);
        return Optional.of(new PartyDetails(party, identifiers));
    }

    // Helper class to encapsulate Party and its Identifiers for retrieval
    public static class PartyDetails {
        private final Party party;
        private final List<Identifier> identifiers;

        public PartyDetails(Party party, List<Identifier> identifiers) {
            this.party = party;
            this.identifiers = Collections.unmodifiableList(new ArrayList<>(identifiers)); // Return immutable list
        }

        public Party getParty() {
            return party;
        }

        public List<Identifier> getIdentifiers() {
            return identifiers;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PartyDetails{\n");
            sb.append("  Party: ").append(party).append("\n");
            sb.append("  Identifiers:\n");
            if (identifiers.isEmpty()) {
                sb.append("    (No identifiers)\n");
            } else {
                identifiers.forEach(id -> sb.append("    - ").append(id).append("\n"));
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
