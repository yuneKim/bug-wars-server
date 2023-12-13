package net.crusadergames.bugwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("users")
public class SampleController {

    @Autowired
    UserRepository userRepository;

    @GetMapping()
    List<SampleUser> getAll() {
        return userRepository.findAll();
    }

    @PostMapping("/add")
    SampleUser addUser(@RequestBody SampleUser newUser) {
        return userRepository.save(newUser);
    }
}
