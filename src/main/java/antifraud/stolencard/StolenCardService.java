package antifraud.stolencard;

import antifraud.exceptions.ConflictException;
import antifraud.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class StolenCardService {
    @Autowired
    StolenCardRepository cardRepository;

    public StolenCard addStolenCard(String cardNumber) {
        if (cardRepository.existsByCardNumber(cardNumber)) {
            throw new ConflictException("Card number already in database");
        }
        return cardRepository.save(new StolenCard(cardNumber));
    }

    public List<StolenCard> getAllStolenCards() {
        return StreamSupport.stream(cardRepository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public void removeStolenCard(String cardNumber) {
        if (!cardRepository.existsByCardNumber(cardNumber)) {
            throw new NotFoundException("Card not found");
        } else {
            cardRepository.deleteByCardNumber(cardNumber);
        }
    }
}
