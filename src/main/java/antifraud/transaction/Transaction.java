package antifraud.transaction;


import antifraud.card.Card;
import antifraud.feedback.Feedback;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Transaction<T> {
    @GeneratedValue
    @Id
    private Long id;
    private @NonNull Long amount;
    private @NonNull String ip;
    private @NonNull Region region;
    private @NonNull LocalDateTime date;
    @Enumerated(EnumType.STRING)
    private @NonNull TransactionStatus status;

    @Embedded
    private Feedback feedback;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    @NonNull
    private Card card;

    public static Transaction<Unprocessed> newUnproccedTransaction(@NonNull Long amount, @NonNull String ip, @NonNull Region region,
                       @NonNull LocalDateTime date, @NonNull Card card) {
        return new Transaction<>(amount, ip, region, date, TransactionStatus.UNPROCESSED, card);
    }

    public Transaction<Validated> process(TransactionStatus status) {
        return new Transaction<>(this.amount, this.ip, this.region, this.date, status, card);
    }
}
