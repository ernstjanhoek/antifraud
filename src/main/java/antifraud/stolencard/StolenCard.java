package antifraud.stolencard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.hibernate.validator.constraints.LuhnCheck;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ResponseStatus
public class StolenCard {
    @Id
    @GeneratedValue
    long id;

    @NonNull
    @LuhnCheck
    @Column(unique=true)
    String cardNumber;
}
