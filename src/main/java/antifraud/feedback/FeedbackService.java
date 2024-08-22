package antifraud.feedback;

import antifraud.card.CardService;
import antifraud.exceptions.ConflictException;
import antifraud.exceptions.NotFoundException;
import antifraud.exceptions.UnprocessableEntityException;
import antifraud.transaction.Transaction;
import antifraud.transaction.TransactionRepository;
import antifraud.transaction.TransactionStatus;
import antifraud.transaction.Validated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FeedbackService {
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CardService cardService;

    public Transaction<Validated> provideFeedback(Long transactionId, TransactionStatus feedback) {
        Optional<Transaction<Validated>> transaction = transactionRepository.findById(transactionId);
        transaction.ifPresentOrElse(tx -> {
            if (tx.getStatus().equals(feedback)) {
                throw new UnprocessableEntityException("Feedback and transaction status can't be equal");
            }
            if (tx.getFeedback() != null) {
                throw new ConflictException("Feedback already exists");
            }
            tx.setFeedback(new Feedback(feedback));
            cardService.updateLimits(tx.getCard(), tx.getStatus(), feedback, tx.getAmount());

            transactionRepository.save(tx);
        }, () -> {
            throw new NotFoundException("No transaction with id " + transactionId + " found!");
        });
        return transaction.get();
    }
}
