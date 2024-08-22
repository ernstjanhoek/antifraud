package antifraud.ip;

import antifraud.exceptions.ConflictException;
import antifraud.exceptions.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class SuspiciousIpService {

    @Autowired
    IpAddressRepository ipAddressRepository;

    public IpAddress addSuspiciousId(String ipAddress) {
        if (ipAddressRepository.existsByIpAddress(ipAddress)) {
            throw new ConflictException("Ip address already exists in database");
        }
        return ipAddressRepository.save(new IpAddress(ipAddress));
    }

    public List<IpAddress> getSuspiciousIpAddresses() {
        return StreamSupport.stream(ipAddressRepository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public void removeSuspiciousIp(String ipAddress) {
        if (!ipAddressRepository.existsByIpAddress(ipAddress)) {
            throw new NotFoundException("Ip address not found in database");
        } else {
            ipAddressRepository.deleteAllByIpAddress(ipAddress);
        }
    }
}
