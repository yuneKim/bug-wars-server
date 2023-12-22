package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.model.SampleString;
import net.crusadergames.bugwars.service.SampleStringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("api/sampleStrings")
public class SampleStringController {
    @Autowired
    SampleStringService sampleStringService;

    @GetMapping
    public List<SampleString> getAll() {
        return sampleStringService.getAll();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    SampleString addString(@RequestBody SampleString newString) {
        return sampleStringService.addString(newString);
    }
}
