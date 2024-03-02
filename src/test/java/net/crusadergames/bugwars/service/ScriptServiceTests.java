package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.ModifyScriptDTO;
import net.crusadergames.bugwars.exception.AccessDeniedException;
import net.crusadergames.bugwars.exception.ResourceConflictException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.exception.ResourceValidationException;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import net.crusadergames.bugwars.repository.ScriptRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ScriptServiceTests {

    private final User USER = new User("tortellini", "gmail@email.com", "passingTests");
    private final Script SCRIPT_1 = new Script(1L, USER, "The Ol' Razzle Dazzle", ":START dance", "20 49 103 952 1039 59 30 10", true);
    private final Script SCRIPT_2 = new Script(2L, USER, "Sneaky Peeky", ":START :END", "03 050 20 50 03 06 10 50", true);
    private final Script SCRIPT_3 = new Script(3L, USER, "Burger Bite", ":START att ifEnemy bite", "05 30 0t 30 f05 52c go2", true);

    private UserRepository userRepository;
    private ScriptRepository scriptRepository;
    private BugAssemblyParserFactory bugAssemblyParserFactory;
    private ScriptService scriptService;

    @BeforeEach
    public void setup() {
        this.scriptRepository = Mockito.mock(ScriptRepository.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.bugAssemblyParserFactory = Mockito.mock(BugAssemblyParserFactory.class);
        this.scriptService = new ScriptService(scriptRepository, userRepository, bugAssemblyParserFactory);
    }

    @Test
    public void getAllNamesOfValidScripts_returnsAllValidScripts() {
        when(scriptRepository.findByIsBytecodeValidTrue()).thenReturn(List.of(
                SCRIPT_1,
                SCRIPT_2
        ));

        List<Script> scripts = scriptService.getAllValidScripts();

        Assertions.assertThat(scripts).hasSize(2);
    }

    @Test
    public void getUserScripts_returnsAllUsersScripts() throws ResourceNotFoundException {
        when(scriptRepository.findByUser(Mockito.any())).thenReturn(List.of(SCRIPT_1, SCRIPT_2));

        User mockUser = new User();
        mockUser.setId(5L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(mockUser));

        List<Script> scripts = scriptService.getUserScripts(Mockito.any());

        Assertions.assertThat(scripts).hasSize(2);
    }

    @Test
    public void getScript_returnsCorrectScript() throws AccessDeniedException, ResourceNotFoundException {
        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_3));

        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));

        Script testScript = scriptService.getScript(3L, Mockito.any());

        Assertions.assertThat(testScript).isEqualTo(SCRIPT_3);
    }

    @Test
    public void getScript_throwsAccessDeniedExceptionOnIncorrectUser() {
        USER.setId(1L);
        User mockUser = new User();
        mockUser.setId(2L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(mockUser));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_3));

        Assertions.assertThatThrownBy(() -> scriptService.getScript(1L, Mockito.any()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void getScript_throwsResourceNotFoundExceptionOnMissingScript() {
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));

        Assertions.assertThatThrownBy(() -> scriptService.getScript(5L, Mockito.any()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void getUser_throwsResourceNotFoundExceptionOnMissingUser() {
        when(userRepository.findByUsername("Guillermo")).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> scriptService.getScript(1L, Mockito.any()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void createScript_throwsResourceConflictExceptionOnDuplicateName() {
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");

        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> scriptService.createScript(request.getName(), request.getRaw(), "Ted"))
                .isInstanceOf(ResourceConflictException.class);
    }

    @Test
    public void createScript_throwsResourceValidationExceptionOnInappropriateName() {
        ModifyScriptDTO request = new ModifyScriptDTO("Fucking Strawberries", ":START\ngoto START");

        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);

        Assertions.assertThatThrownBy(() -> scriptService.createScript(request.getName(), request.getRaw(), "Ted"))
                .isInstanceOf(ResourceValidationException.class);
    }


    @Test
    public void createScript_returnsCreatedScript() throws BugAssemblyParseException, ResourceConflictException, ResourceValidationException, ResourceNotFoundException {
        List<Integer> expectedResult = List.of(35, 0);
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");


        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenReturn(expectedResult);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(SCRIPT_1);

        Script createdScript = scriptService.createScript(request.getName(), request.getRaw(), "Ted");

        Assertions.assertThat(createdScript).isNotNull();
    }

    @Test
    public void createScript_handlesInvalidByteCode() throws BugAssemblyParseException, ResourceConflictException, ResourceValidationException, ResourceNotFoundException {
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        Script testScript = new Script(1L, new User(), "Highway Snobbery", ":START wiggle", "", false);


        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.existsByNameIgnoreCase(Mockito.any())).thenReturn(false);
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        when(bugAssemblyParser.parse(request.getRaw())).thenThrow(BugAssemblyParseException.class);
        when(scriptRepository.save(Mockito.any(Script.class))).thenReturn(testScript);

        Script createdScript = scriptService.createScript(request.getName(), request.getRaw(), "Ted");

        Assertions.assertThat(createdScript.isBytecodeValid()).isFalse();
    }

    @Test
    public void updateScript_returnsUpdatedScript() throws BugAssemblyParseException, AccessDeniedException, ResourceValidationException, ResourceNotFoundException {
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

        Script updatedScript = scriptService.updateScript(1L, request.getName(), request.getRaw(), "Ted");

        Assertions.assertThat(updatedScript).isNotNull();
    }

    @Test
    public void updateScript_throwsResourceNotFoundExceptionWhenScriptDoesNotExist() {
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, request.getName(), request.getRaw(), "Esteban"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    public void updateScript_throwsAccessDeniedExceptionWhenUnauthorized() {
        BugAssemblyParser bugAssemblyParser = mock(BugAssemblyParser.class);
        ModifyScriptDTO request = new ModifyScriptDTO("Highway Robbery", ":START\ngoto START");
        User user = new User();
        user.setId(1L);
        USER.setId(2L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));
        when(bugAssemblyParserFactory.createInstance()).thenReturn(bugAssemblyParser);
        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, request.getName(), request.getRaw(), "Esteban"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void updateScript_handlesInvalidByteCode() throws BugAssemblyParseException, AccessDeniedException, ResourceValidationException, ResourceNotFoundException {
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
        Script updatedScript = scriptService.updateScript(1L, request.getName(), request.getRaw(), "Esteban");

        Assertions.assertThat(updatedScript.isBytecodeValid()).isFalse();
    }

    @Test
    public void updateScript_throwsResourceValidationExceptionOnInappropriateName() throws BugAssemblyParseException, JsonProcessingException {
        ModifyScriptDTO request = new ModifyScriptDTO("Fucking Robbery", ":START\ngoto START");
        USER.setId(1L);
        when(userRepository.findByUsername(Mockito.any())).thenReturn(Optional.of(USER));
        when(scriptRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(SCRIPT_1));

        Assertions.assertThatThrownBy(() -> scriptService.updateScript(1L, request.getName(), request.getRaw(), "Ted"))
                .isInstanceOf(ResourceValidationException.class);
    }


    @Test
    public void deleteScriptById_throwsAccessDeniedExceptionWhenUserIsUnauthorized() {
        User user = new User();
        user.setId(2L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_1));

        Assertions.assertThatThrownBy(() -> scriptService.deleteScriptById(1L, "Esteban"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void deleteScriptById_doesNotDeleteIfScriptDoesNotExist() throws AccessDeniedException, ResourceNotFoundException {
        User user = new User();
        user.setId(1L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        scriptService.deleteScriptById(1L, "Esteban");

        Mockito.verify(scriptRepository, times(0)).deleteById(Mockito.any());
    }

    @Test
    public void deleteScriptById_deletesCorrectScript() throws AccessDeniedException, ResourceNotFoundException {
        User user = new User();
        user.setId(1L);
        USER.setId(1L);
        when(userRepository.findByUsername("Esteban")).thenReturn(Optional.of(user));

        when(scriptRepository.findById(Mockito.any())).thenReturn(Optional.of(SCRIPT_1));
        scriptService.deleteScriptById(1L, "Esteban");

        Mockito.verify(scriptRepository, times(1)).deleteById(Mockito.any());
    }

}
