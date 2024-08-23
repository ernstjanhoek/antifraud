package antifraud.controller;

import antifraud.user.Authority;
import antifraud.user.User;
import antifraud.user.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    UserDetailsServiceImpl userService;

    @DeleteMapping("/user/{username}")
    public UserStatusResponse deleteUser(@PathVariable String username) {
        return new UserStatusResponse(username, userService.deleteUser(username) ? "Deleted successfully!" : "Deletion failed.");
    }

    @PostMapping("/user")
    @CrossOrigin()
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse registerUser(@Valid @RequestBody NewUserRequest request) {
        return UserResponse.fromUser(userService.createUser(request.name(), request.username(), request.password()));
    }

    @GetMapping("/cred")
    @CrossOrigin(origins = "http://127.0.0.1:4200", allowCredentials = "true")
    public void checkCreds() {}

    @GetMapping("/list")
    public List<UserResponse> getUsers() {
        return userService.getAllUsers().stream()
                .map(UserResponse::fromUser)
                .toList();
    }

    @PutMapping("/access")
    public StatusResponse changeAccess(@Valid @RequestBody UserAccessRequest request) {
        boolean lockUser = request.operation().isLocked();
        User user = userService.lockUserAccess(request.username(), lockUser);
        return new StatusResponse("User %s %s!".formatted(user.getUsername(), lockUser ? "locked" : "unlocked"));
    }

    @PutMapping("/role")
    public UserResponse changeRole(@Valid @RequestBody UserRoleRequest request) {
        return UserResponse.fromUser(userService.changeRole(request.username(), request.role()));
    }

    public record UserResponse(long id, String name, String username, Authority role) {
        public static UserResponse fromUser(User user) {
            return new UserResponse(user.getId(), user.getName(), user.getUsername(), user.getAuthority());
        }
    }

    public record UserStatusResponse(String username, String status) {}

    public record UserRoleRequest(String username, Authority role) {}

    public record NewUserRequest(@NotEmpty String name, @NotEmpty String username, @NotEmpty String password) {}

    public record UserAccessRequest(String username, Operation operation) { }

    enum Operation {
        LOCK, UNLOCK;

        public boolean isLocked() {
            return this.equals(LOCK);
        }
    }
}
