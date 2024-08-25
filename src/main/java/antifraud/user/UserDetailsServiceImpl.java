package antifraud.user;

import antifraud.exceptions.BadRequestException;
import antifraud.exceptions.NotFoundException;
import antifraud.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository
                .findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        return new UserAdapter(user);
    }

    public User changeRole(String username, Authority role) {
        Optional<User> user = repository.findUserByUsername(username);

        if (role.equals(Authority.ADMINISTRATOR)) {
            throw new BadRequestException("Only one administrator allowed!");
        }

        if (user.isPresent()) {
            if (role.equals(user.get().getAuthority())) {
                throw new ConflictException("User already has authority " + user.get().getAuthority());
            }
            if (user.get().getAuthority().equals(Authority.ADMINISTRATOR)) {
                throw new BadRequestException("Cannot change role of administrator");
            }
            user.get().setAuthority(role);
            userRepository.save(user.get());

        } else {
            throw new NotFoundException("User not found!");
        }
        return user.get();
    }

    public User lockUserAccess(String username, boolean locked) {
        Optional<User> user = repository.findUserByUsername(username);
        if (user.isPresent()) {
            if (user.get().getAuthority().equals(Authority.ADMINISTRATOR)) {
                throw new BadRequestException("Cannot lock administrator");
            }
            user.get().setLocked(locked);
            userRepository.save(user.get());
        } else {
            throw new NotFoundException("User not found!");
        }
        return user.get();
    }

    public User createUser(String name, String username, String password) {
        long userCount = userRepository.count();

        Authority role;
        boolean lockState;

        if (userCount == 0) {
            role = Authority.ADMINISTRATOR;
            lockState = false;
        } else {
            role = Authority.MERCHANT;
            lockState = true;
        }

        User user = new User(name, username, passwordEncoder.encode(password), role, lockState);

        try {
            this.repository.save(user);
            return user;
        } catch (Exception e) {
            throw new ConflictException("Username already exists");
        }
    }

    public List<User> getAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }

    @Transactional
    public boolean deleteUser(String username) {
        Optional<User> userOptional = repository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            if (userOptional.get().getAuthority().equals(Authority.ADMINISTRATOR)) {
                throw new BadRequestException("Cannot delete administrator");
            }
            return userRepository.deleteUserByUsername(username) > 0;
        } else {
            throw new NotFoundException("User not found!");
        }
    }

    public User findUser(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
    }
}