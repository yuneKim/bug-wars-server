package net.crusadergames.bugwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("users")
public class SampleController {

    @Autowired
    SampleService sampleService;

    @GetMapping()
    List<SampleUser> getAll() {
        return sampleService.getAll();
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    SampleUser addUser(@RequestBody SampleUser newUser) {
        return sampleService.addUser(newUser);
    }
}
