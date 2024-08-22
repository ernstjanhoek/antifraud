package antifraud.feedback;

import antifraud.transaction.TransactionStatus;

public record FeedbackRequest(Long transactionId, TransactionStatus feedback) {}