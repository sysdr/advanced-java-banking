package com.bank.identity;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Identifier { // Made final to emphasize immutability
    private final String identifierId;
    private final String partyId;
    private final IdentifierType type;
    private final String value;
    private final LocalDate effectiveDate;
    private final IdentifierStatus status; // Status can change, but it's a new "version" if the entire record changes. For this demo, status is part of the immutable record.

    public Identifier(String partyId, IdentifierType type, String value, LocalDate effectiveDate, IdentifierStatus status) {
        this.identifierId = UUID.randomUUID().toString();
        this.partyId = partyId;
        this.type = type;
        this.value = value;
        this.effectiveDate = effectiveDate;
        this.status = status;
    }

    // Constructor for creating new immutable versions (e.g., for status change)
    private Identifier(String identifierId, String partyId, IdentifierType type, String value, LocalDate effectiveDate, IdentifierStatus status) {
        this.identifierId = identifierId;
        this.partyId = partyId;
        this.type = type;
        this.value = value;
        this.effectiveDate = effectiveDate;
        this.status = status;
    }

    public Identifier withStatus(IdentifierStatus newStatus) {
        return new Identifier(this.identifierId, this.partyId, this.type, this.value, this.effectiveDate, newStatus);
    }

    public String getIdentifierId() {
        return identifierId;
    }

    public String getPartyId() {
        return partyId;
    }

    public IdentifierType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public IdentifierStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return identifierId.equals(that.identifierId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifierId);
    }

    @Override
    public String toString() {
        return "Identifier{" +
               "identifierId='" + identifierId + '\'' +
               ", partyId='" + partyId + '\'' +
               ", type=" + type +
               ", value='" + value + '\'' +
               ", effectiveDate=" + effectiveDate +
               ", status=" + status +
               '}';
    }
}
