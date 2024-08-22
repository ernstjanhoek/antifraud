package antifraud.transaction;

import antifraud.card.Card;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction<Validated>, Long> {
    Integer countDistinctByCardAndDateBetweenAndRegionNot(Card card, LocalDateTime startTime, LocalDateTime endTime, Region region);
    Integer countDistinctByCardAndDateBetweenAndIpNot(Card card, LocalDateTime startTime, LocalDateTime endTime, String ip);
    List<Transaction<Validated>> findAllByCard(Card card);
}