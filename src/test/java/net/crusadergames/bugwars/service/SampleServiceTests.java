package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.SampleString;
import net.crusadergames.bugwars.repository.SampleStringRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SampleServiceTests {
    @Mock
    private SampleStringRepository sampleStringRepository;

    @InjectMocks
    private SampleStringService sampleStringService;

    @Test
    public void getAll_returnsAllStrings() {
        SampleString string = SampleString.builder().content("some string").build();
        when(sampleStringRepository.findAll()).thenReturn(List.of(string));

        List<SampleString> strings = sampleStringService.getAll();

        Assertions.assertThat(strings).hasSize(1).contains(string);
    }

    @Test
    public void addUser_returnsCreatedString() {
        SampleString string = SampleString.builder().content("porcupine").build();
        when(sampleStringRepository.save(Mockito.any(SampleString.class))).thenReturn(string);

        SampleString savedString = sampleStringService.addString(string);

        Assertions.assertThat(savedString).isNotNull();
    }
}
