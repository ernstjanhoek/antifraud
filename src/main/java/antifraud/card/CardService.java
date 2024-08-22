package antifraud.card;

import antifraud.transaction.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardService {

    @Autowired
    CardRepository cardRepository;

    public void updateLimits(Card card, TransactionStatus status, TransactionStatus feedback, Long amount) {
        if (feedback.equals(TransactionStatus.ALLOWED) && status.equals(TransactionStatus.MANUAL_PROCESSING)) {
            card.setAllowedLimit(increaseLimit(card.getAllowedLimit(), amount));
        }

        if (feedback.equals(TransactionStatus.ALLOWED) && status.equals(TransactionStatus.PROHIBITED)) {
            card.setAllowedLimit(increaseLimit(card.getAllowedLimit(), amount));
            card.setManualLimit(increaseLimit(card.getManualLimit(), amount));
        }

        if (feedback.equals(TransactionStatus.MANUAL_PROCESSING) && status.equals(TransactionStatus.ALLOWED)) {
            card.setAllowedLimit(decreaseLimit(card.getAllowedLimit(), amount));
        }

        if (feedback.equals(TransactionStatus.MANUAL_PROCESSING) && status.equals(TransactionStatus.PROHIBITED)) {
            card.setManualLimit(increaseLimit(card.getManualLimit(), amount));
        }

        if (feedback.equals(TransactionStatus.PROHIBITED) && status.equals(TransactionStatus.ALLOWED)) {
            card.setAllowedLimit(decreaseLimit(card.getAllowedLimit(), amount));
            card.setManualLimit(decreaseLimit(card.getManualLimit(), amount));
        }

        if (feedback.equals(TransactionStatus.PROHIBITED) && status.equals(TransactionStatus.MANUAL_PROCESSING)) {
            card.setManualLimit(decreaseLimit(card.getManualLimit(), amount));
        }

    }

    private int increaseLimit(long current_limit, long transactionValue) {
        return (int) Math.ceil(0.8 * current_limit + 0.2 * transactionValue);
    }

    private int decreaseLimit(long current_limit, long transactionValue) {
        return (int) Math.ceil(0.8 * current_limit - 0.2 * transactionValue);
    }


}
