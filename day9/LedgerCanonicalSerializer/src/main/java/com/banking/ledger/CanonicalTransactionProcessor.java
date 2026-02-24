package com.banking.ledger;

import com.banking.ledger.proto.TransactionProto.Transaction;
import com.banking.ledger.proto.TransactionProto;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class CanonicalTransactionProcessor {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidProtocolBufferException {
        System.out.println("==========================================================");
        System.out.println("  Canonical Serialization Demo for Banking Transactions   ");
        System.out.println("==========================================================");

        // --- 1. Create two logically identical transactions ---
        System.out.println("\n--- Step 1: Creating two logically identical transactions ---");
        long currentTimestamp = System.currentTimeMillis();

        Transaction tx1 = Transaction.newBuilder()
            .setTransactionId("TX-1234567890")
            .setAccountId("ACC-987654321")
            .setAmountMinorUnits(50000) // 500.00 USD
            .setCurrency("USD")
            .setTimestampEpochMillis(currentTimestamp)
            .setDescription("Online purchase from RetailerX")
            .setType(TransactionProto.TransactionType.DEBIT)
            .addRelatedTransactionIds("INV-XYZ-001")
            .addRelatedTransactionIds("PAY-REF-ABC")
            .build();

        // Create tx2 as an identical copy of tx1
        Transaction tx2 = Transaction.newBuilder(tx1).build();

        System.out.println("Transaction 1 (Logical):\n" + formatTransaction(tx1));
        System.out.println("Transaction 2 (Logical):\n" + formatTransaction(tx2));

        // --- 2. Serialize transactions to byte arrays ---
        System.out.println("\n--- Step 2: Serializing transactions to byte arrays ---");
        byte[] serializedTx1 = tx1.toByteArray();
        byte[] serializedTx2 = tx2.toByteArray();

        System.out.println("Serialized Tx1 (Base64): " + Base64.getEncoder().encodeToString(serializedTx1));
        System.out.println("Serialized Tx2 (Base64): " + Base64.getEncoder().encodeToString(serializedTx2));

        // --- 3. Compute SHA-256 hashes of serialized bytes ---
        System.out.println("\n--- Step 3: Computing SHA-256 hashes ---");
        String hash1 = sha256Hash(serializedTx1);
        String hash2 = sha256Hash(serializedTx2);

        System.out.println("SHA-256 Hash of Tx1: " + hash1);
        System.out.println("SHA-256 Hash of Tx2: " + hash2);

        // --- 4. Verify Determinism ---
        System.out.println("\n--- Step 4: Verifying Determinism ---");
        boolean bytesMatch = Arrays.equals(serializedTx1, serializedTx2);
        boolean hashesMatch = hash1.equals(hash2);

        System.out.println("Are serialized byte arrays identical? " + (bytesMatch ? "YES" : "NO"));
        System.out.println("Are SHA-256 hashes identical?        " + (hashesMatch ? "YES" : "NO"));

        if (bytesMatch && hashesMatch) {
            System.out.println("Conclusion: Canonical serialization achieved! Identical objects produce identical byte streams and hashes.");
        } else {
            System.out.println("Conclusion: Serialization is NOT canonical. This is a critical issue for ledger integrity!");
            System.exit(1);
        }

        // --- 5. Deserialize and Verify Roundtrip ---
        System.out.println("\n--- Step 5: Deserializing and Verifying Roundtrip ---");
        Transaction deserializedTx = Transaction.parseFrom(serializedTx1);
        System.out.println("Deserialized Transaction (Logical):\n" + formatTransaction(deserializedTx));

        boolean roundtripMatch = tx1.equals(deserializedTx);
        System.out.println("Does deserialized transaction match original? " + (roundtripMatch ? "YES" : "NO"));

        if (roundtripMatch) {
            System.out.println("Conclusion: Serialization and deserialization roundtrip is successful.");
        } else {
            System.out.println("Conclusion: Deserialized object does not match original. Roundtrip failed!");
            System.exit(1);
        }

        System.out.println("\n==========================================================");
        System.out.println("  Demo Complete!                                          ");
        System.out.println("==========================================================");
    }

    private static String sha256Hash(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return Base64.getEncoder().encodeToString(hash);
    }

    private static String formatTransaction(Transaction tx) {
        return String.format(
            "  ID: %s\n  Account: %s\n  Amount: %.2f %s\n  Timestamp: %d\n  Description: %s\n  Type: %s\n  Related IDs: %s",
            tx.getTransactionId(),
            tx.getAccountId(),
            tx.getAmountMinorUnits() / 100.0,
            tx.getCurrency(),
            tx.getTimestampEpochMillis(),
            tx.getDescription(),
            tx.getType(),
            tx.getRelatedTransactionIdsList()
        );
    }
}
