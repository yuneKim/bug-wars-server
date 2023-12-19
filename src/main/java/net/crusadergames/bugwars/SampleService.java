package net.crusadergames.bugwars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleService {
    @Autowired
    SampleUserRepository sampleUserRepository;

    public List<SampleUser> getAll() {
        return sampleUserRepository.findAll();
    }

    public SampleUser addUser(SampleUser newUser) {
        return sampleUserRepository.save(newUser);
    }
}
