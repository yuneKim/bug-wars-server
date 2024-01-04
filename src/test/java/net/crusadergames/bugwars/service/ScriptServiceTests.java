package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.ScriptRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScriptServiceTests {

    private final User USER = new User("tortellini", "gmail@email.com", "passingTests");
    private final Script SCRIPT_1 = new Script(1L, USER, "The Ol' Razzle Dazzle", ":START dance", "20 49 103 952 1039 59 30 10", true);
    private final Script SCRIPT_2 = new Script(2L, USER, "Sneaky Peeky", ":START :END", "03 050 20 50 03 06 10 50", true);
    private final Script SCRIPT_3 = new Script(3L, USER, "Burger Bite", ":START att ifEnemy bite", "05 30 0t 30 f05 52c go2", true);


    @Mock
    private UserRepository userRepository;

    @Mock
    private ScriptRepository scriptRepository;

    @InjectMocks
    private ScriptService scriptService;

    @Test
    public void getUserScripts_returnsAllUsersScripts() {
        when(scriptRepository.findByUser(Mockito.any())).thenReturn(List.of(SCRIPT_1, SCRIPT_2));

        User mockUser = new User();
        mockUser.setId(5L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(mockUser));

        List<Script> scripts = scriptService.getUserScripts(Mockito.mock(Principal.class));

        Assertions.assertThat(scripts).hasSize(2);
    }

    @Test
    public void getScript_returnsCorrectScript() {
        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_3));

        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));

        Script testScript = scriptService.getScript(3L, Mockito.mock(Principal.class));

        Assertions.assertThat(testScript).isEqualTo(SCRIPT_3);
    }

    @Test
    public void getScript_respondsWithForbiddenStatusOnIncorrectUser() {
        USER.setId(1L);
        User mockUser = new User();
        mockUser.setId(2L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(mockUser));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_3));

        Assertions.assertThatThrownBy(() -> scriptService.getScript(1L,Mockito.mock(Principal.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
    }


}
