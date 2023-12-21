package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.SampleString;
import net.crusadergames.bugwars.repository.auth.SampleStringRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleStringService {
    @Autowired
    SampleStringRepository sampleStringRepository;

    public List<SampleString> getAll() {
        return sampleStringRepository.findAll();
    }

    public SampleString addString(SampleString newString) {
        return sampleStringRepository.save(newString);
    }
}
