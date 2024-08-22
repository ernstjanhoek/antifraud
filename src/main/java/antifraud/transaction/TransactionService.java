package antifraud.transaction;

import antifraud.card.Card;
import antifraud.card.CardRepository;
import antifraud.exceptions.NotFoundException;
import antifraud.ip.IpAddressRepository;
import antifraud.stolencard.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class TransactionService {
    @Autowired
    StolenCardRepository stolenCardRepository;

    @Autowired
    IpAddressRepository ipAddressRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CardRepository cardRepository;

    private final Duration TIME_RANGE = Duration.ofSeconds(3600);
    private final int CORRELATION_LIMIT = 3;

    public List<Transaction<Validated>> getTransactionsByCardNumber(String number) {
        Optional<Card> cardOptional = cardRepository.findByCardNumber(number);
        if (cardOptional.isPresent()) {
            return transactionRepository.findAllByCard(cardOptional.get());
        } else {
            throw new NotFoundException("Card not found");
        }
    }

    public List<Transaction<Validated>> getAllTransactions() {
        return StreamSupport.stream(transactionRepository.findAll().spliterator(), false).toList();
    }

    public TransactionStatusWithInfo addTransaction(TransactionRequest request) {
        Optional<Card> cardOptional = cardRepository.findByCardNumber(request.number());
        Card card;

        if (cardOptional.isEmpty()) {
            card = new Card(request.number());
            cardRepository.save(card);
        } else {
            card = cardOptional.get();
        }

        Transaction<Unprocessed> transaction = Transaction.newUnproccedTransaction(
                request.amount(),
                request.ip(),
                request.region(),
                request.date(),
                card);

        return processTransaction(transaction);
    }

    private int regionCorrelationCheck(Transaction<?> transaction) {
        return transactionRepository.countDistinctByCardAndDateBetweenAndRegionNot(
                transaction.getCard(),
                transaction.getDate().minus(TIME_RANGE),
                transaction.getDate(),
                transaction.getRegion()
        );
    }

    private int ipCorrelationCheck(Transaction<?> transaction) {
        return transactionRepository.countDistinctByCardAndDateBetweenAndIpNot(
                transaction.getCard(),
                transaction.getDate().minus(TIME_RANGE),
                transaction.getDate(),
                transaction.getIp());
    }

    public TransactionStatusWithInfo processTransaction(Transaction<?> transaction) {
        boolean stolenCardUsed = stolenCardRepository.existsByCardNumber(transaction.getCard().getCardNumber());
        boolean suspiciousIpAddressUsed = ipAddressRepository.existsByIpAddress(transaction.getIp());
        int ipCorrelation = ipCorrelationCheck(transaction);
        int regionCorrelation = regionCorrelationCheck(transaction);
        long transactionAmount = transaction.getAmount();
        int MANUAL_LIMIT = transaction.getCard().getManualLimit();
        int ALLOWED_LIMIT = transaction.getCard().getAllowedLimit();

        List<String> prohibitedInfo = new ArrayList<>();
        List<String> manualInfo = new ArrayList<>();
        if (stolenCardUsed) prohibitedInfo.add("card-number");

        if (suspiciousIpAddressUsed) prohibitedInfo.add("ip");

        if (ipCorrelation > CORRELATION_LIMIT) prohibitedInfo.add("ip-correlation");

        if (regionCorrelation > CORRELATION_LIMIT) prohibitedInfo.add("region-correlation");

        if (transactionAmount > ALLOWED_LIMIT && transactionAmount <= MANUAL_LIMIT)
            manualInfo.add("amount");

        if (transactionAmount > MANUAL_LIMIT) prohibitedInfo.add("amount");

        if (ipCorrelation == CORRELATION_LIMIT) manualInfo.add("ip-correlation");

        if (regionCorrelation == CORRELATION_LIMIT) manualInfo.add("region-correlation");

        List<String> info = new ArrayList<>();
        TransactionStatus status;
        if (!prohibitedInfo.isEmpty()) {
            info = prohibitedInfo;
            status = TransactionStatus.PROHIBITED;
        } else if (!manualInfo.isEmpty()) {
            info = manualInfo;
            status = TransactionStatus.MANUAL_PROCESSING;
        } else {
            info.add("none");
            status = TransactionStatus.ALLOWED;
        }

        Transaction<Validated> validatedTransaction = transaction.process(status);
        transactionRepository.save(validatedTransaction);

        Collections.sort(info);
        return new TransactionStatusWithInfo(status, String.join(", ", info));
    }
}
