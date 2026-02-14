package com.bank.identity;

import java.util.Objects;
import java.util.UUID;

public class Party {
    private final String partyId;
    private String name;

    public Party(String name) {
        this.partyId = UUID.randomUUID().toString();
        this.name = name;
    }

    public Party(String partyId, String name) {
        this.partyId = partyId;
        this.name = name;
    }

    public String getPartyId() {
        return partyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Party party = (Party) o;
        return partyId.equals(party.partyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(partyId);
    }

    @Override
    public String toString() {
        return "Party{" +
               "partyId='" + partyId + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
