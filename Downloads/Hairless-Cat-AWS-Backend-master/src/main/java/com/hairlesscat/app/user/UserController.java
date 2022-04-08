package com.hairlesscat.app.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.hairlesscat.app.util.CopyClass;
import com.hairlesscat.app.util.ResponseWrapper;
import com.hairlesscat.app.view.Views;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path="{user_id}")
    @JsonView(Views.UserFull.class)
    public User getUserById(@PathVariable("user_id") String userId) {
        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no user found with id " + userId);
        }
    }

    @GetMapping
    @JsonView(Views.UserSummary.class)
    public Map<String, List<User>> getUsers() {
        List<User> users = userService.getUsers();
        return ResponseWrapper.wrapResponse("users", users);
    }

    @PostMapping
    @JsonView(Views.UserFull.class)
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping(path="{user_id}")
    @JsonView(Views.UserFull.class)
    public User updateUser(@RequestBody User user, @PathVariable("user_id") String userId) {
        User existingUser = userService
                .getUserById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id " + userId));

        //copy only non null values
        BeanUtils.copyProperties(user, existingUser, CopyClass.getNullPropertyNames(user));

        return userService.updateUser(existingUser);
    }

    @DeleteMapping(path="{user_id}")
    public void deleteUser(@PathVariable("user_id") String userId) {
        userService.deleteUser(userId);
    }
}
