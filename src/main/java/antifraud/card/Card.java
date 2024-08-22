package antifraud.card;

import antifraud.transaction.Transaction;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String cardNumber;

    private int allowedLimit;

    private int manualLimit;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL)
    private List<Transaction> transaction;

    public Card(String number) {
        this.cardNumber = number;
        this.allowedLimit = 200;
        this.manualLimit = 1500;
    }
}
