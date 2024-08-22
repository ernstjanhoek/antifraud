package antifraud.transaction;

import java.time.LocalDateTime;

public record TxWithFeedbackResponse(
        Long transactionId,
        Long amount,
        String ip,
        String number,
        Region region,
        LocalDateTime date,
        TransactionStatus result,
        String feedback) {

    static public TxWithFeedbackResponse fromTransaction(Transaction tx) {
        String feedback;

        if (tx.getFeedback() == null) {
            feedback = "";
        } else {
            feedback = tx.getFeedback().getFeedback().toString();
        }

        return new TxWithFeedbackResponse(
                tx.getId(),
                tx.getAmount(),
                tx.getIp(),
                tx.getCard().getCardNumber(),
                tx.getRegion(),
                tx.getDate(),
                tx.getStatus(),
                feedback
        );
    }
}