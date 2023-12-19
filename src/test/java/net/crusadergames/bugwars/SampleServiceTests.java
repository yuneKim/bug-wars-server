package net.crusadergames.bugwars;

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
    private SampleUserRepository sampleUserRepository;

    @InjectMocks
    private SampleService sampleService;

    @Test
    public void getAll_returnsAllUsers() {
        SampleUser user = SampleUser.builder().username("Charlie").build();
        when(sampleUserRepository.findAll()).thenReturn(List.of(user));

        List<SampleUser> users = sampleService.getAll();

        Assertions.assertThat(users).hasSize(1).contains(user);
    }

    @Test
    public void addUser_returnsCreatedUser() {
        SampleUser user = SampleUser.builder().username("Fred").build();
        when(sampleUserRepository.save(Mockito.any(SampleUser.class))).thenReturn(user);

        SampleUser savedUser = sampleService.addUser(user);

        Assertions.assertThat(savedUser).isNotNull();
    }
}
