package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ModifyScriptDTO;
import net.crusadergames.bugwars.dto.response.ScriptNameDTO;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

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

    @Mock
    private BugAssemblyParserFactory bugAssemblyParserFactory;
    @InjectMocks
    private ScriptService scriptService;

    @Test
    public void getAllNamesOfValidScripts_returnsAllValidScripts() {
        when(scriptRepository.findByIsBytecodeValidTrue()).thenReturn(List.of(
                SCRIPT_1,
                SCRIPT_2
        ));

        List<ScriptNameDTO> scripts = scriptService.getAllNamesOfValidScripts();

        Assertions.assertThat(scripts).hasSize(2);
    }

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

        Assertions.assertThatThrownBy(() -> scriptService.getScript(1L, Mockito.mock(Principal.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
    }

    @Test
    public void getScript_respondsWithNotFoundOnMissingScript() {
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));

        Assertions.assertThatThrownBy(() -> scriptService.getScript(5L, Mockito.mock(Principal.class)))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void getUser_respondsWithServerErrorOnMissingUser() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("Guillermo");
        when(userRepository.findByUsername("Guillermo")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> scriptService.getScript(1L, mockPrincipal))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void createScript_respondsWithConflictStatusOnDuplicateName() {
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");


        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> scriptService.createScript(request, mockPrincipal))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.CONFLICT);
    }

    @Test
    public void createScript_respondsWithConflictStatusOnInappropriateName() {
        ModifyScriptDTO request = new ModifyScriptDTO("Fucking Strawberries", ":START\ngoto START");

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> scriptService.createScript(request, mockPrincipal))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }


    @Test
    public void createScript_returnsCreatedScript() throws BugAssemblyParseException, JsonProcessingException {
        List<Integer> expectedResult = List.of(35, 0);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");


        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenReturn(expectedResult);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(SCRIPT_1);

        Script createdScript = scriptService.createScript(request, mockPrincipal);

        Assertions.assertThat(createdScript).isNotNull();
    }

    @Test
    public void createScript_handlesInvalidByteCode() throws BugAssemblyParseException {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        List<Integer> emptyList = new ArrayList<>();
        Script testScript = new Script(1L, new User(), "Highway Snobbery", ":START wiggle", "", false);


        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenThrow(BugAssemblyParseException.class);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(testScript);

        Script createdScript = scriptService.createScript(request, mockPrincipal);

        Assertions.assertThat(createdScript.isBytecodeValid()).isFalse();
    }

    @Test
    public void updateScript_returnsUpdatedScript() throws BugAssemblyParseException, JsonProcessingException {
        List<Integer> expectedResult = List.of(35, 0);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenReturn(expectedResult);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(SCRIPT_1);

        Script updatedScript = scriptService.updateScript(1L, mockPrincipal, request);

        Assertions.assertThat(updatedScript).isNotNull();
    }

    @Test
    public void updateScript_throwsNotFoundWhenScriptDoesNotExist() throws BugAssemblyParseException, JsonProcessingException {
        List<Integer> expectedResult = List.of(35, 0);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, mockPrincipal, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.NOT_FOUND);
    }

    @Test
    public void updateScript_throwsForbiddenWhenUnauthorized() throws BugAssemblyParseException, JsonProcessingException {
        List<Integer> expectedResult = List.of(35, 0);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        User user = new User();
        user.setId(1L);
        USER.setId(2L);
        when(mockPrincipal.getName()).thenReturn("Esteban");
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, mockPrincipal, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateScript_handlesInvalidByteCode() throws BugAssemblyParseException, JsonProcessingException {
        List<Integer> expectedResult = List.of(35, 0);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        Script testScript = new Script(1L, new User(), "Highway Snobbery", ":START wiggle", "", false);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        User user = new User();
        user.setId(1L);
        USER.setId(1L);
        when(mockPrincipal.getName()).thenReturn("Esteban");
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenThrow(BugAssemblyParseException.class);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(testScript);
        Script updatedScript = scriptService.updateScript(1L, mockPrincipal, request);

        Assertions.assertThat(updatedScript.isBytecodeValid()).isFalse();
    }

    @Test
    public void updateScript_throwsConflictStatusOnInappropriateName() throws BugAssemblyParseException, JsonProcessingException {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Fucking Robbery", ":START\ngoto START");
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));

        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, mockPrincipal, request))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.BAD_REQUEST);
    }


    @Test
    public void deleteScriptById_returnsForbiddenStatusWhenUserIdsDontMatch() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("Esteban");

        User user = new User();
        user.setId(2L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_1));

        Assertions.assertThatThrownBy(() -> scriptService.deleteScriptById(1L, mockPrincipal))
                .isInstanceOf(ResponseStatusException.class)
                .hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteScriptById_doesNotDeleteIfScriptDoesNotExist() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("Esteban");

        User user = new User();
        user.setId(1L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        scriptService.deleteScriptById(1L, mockPrincipal);

        Mockito.verify(scriptRepository, times(0)).deleteById(Mockito.any());
    }

    @Test
    public void deleteScriptById_deletesCorrectScript() {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("Esteban");

        User user = new User();
        user.setId(1L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_1));
        scriptService.deleteScriptById(1L, mockPrincipal);

        Mockito.verify(scriptRepository, times(1)).deleteById(Mockito.any());
    }

}
