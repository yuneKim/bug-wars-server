package net.crusadergames.bugwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleService {
    @Autowired
    UserRepository userRepository;

    public List<SampleUser> getAll() {
        return userRepository.findAll();
    }

    public SampleUser addUser(SampleUser newUser) {
        return userRepository.save(newUser);
    }
}
