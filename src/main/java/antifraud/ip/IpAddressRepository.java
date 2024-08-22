package antifraud.ip;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface IpAddressRepository extends CrudRepository<IpAddress, Long> {
    Optional<IpAddress> findByIpAddress(String ipAddress);
    boolean existsByIpAddress(String ipAddress);
    long deleteAllByIpAddress(String address);
}
