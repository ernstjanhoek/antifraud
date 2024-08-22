package antifraud.transaction;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.LuhnCheck;

import java.time.LocalDateTime;

public record TransactionRequest(
        @Min(1) long amount,

        @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}")
        String ip,

        @LuhnCheck String number,

        Region region,

        LocalDateTime date
) {}