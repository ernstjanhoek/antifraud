package antifraud.card;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface CardRepository extends CrudRepository<Card, Long> {
    Optional<Card> findByCardNumber(String cardNumber);
}
