package antifraud.feedback;

import antifraud.transaction.TransactionStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class Feedback {
    @NonNull
    @Enumerated(EnumType.STRING)
    private TransactionStatus feedback;
}
