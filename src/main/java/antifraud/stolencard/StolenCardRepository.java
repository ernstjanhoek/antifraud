package antifraud.stolencard;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface StolenCardRepository extends CrudRepository<StolenCard, Integer> {
    Optional<StolenCard> findByCardNumber(String stolenCard);
    boolean existsByCardNumber(String stolenCard);
    void deleteByCardNumber(String stolenCard);
}
