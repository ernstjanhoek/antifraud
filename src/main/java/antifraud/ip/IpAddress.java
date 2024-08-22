package antifraud.ip;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class IpAddress {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Pattern(regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}")
    @Column(unique = true)
    private String ipAddress;
}
