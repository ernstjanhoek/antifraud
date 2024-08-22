package antifraud.transaction;

public record TransactionStatusWithInfo(
        TransactionStatus result,
        String info) {}