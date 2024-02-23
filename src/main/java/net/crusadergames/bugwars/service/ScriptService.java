package net.crusadergames.bugwars.service;

import com.modernmt.text.profanity.ProfanityFilter;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    private final ScriptRepository scriptRepository;
    private final UserRepository userRepository;
    private final BugAssemblyParserFactory bugAssemblyParserFactory;

    public ScriptService(ScriptRepository scriptRepository, UserRepository userRepository,
                         BugAssemblyParserFactory bugAssemblyParserFactory) {
        this.scriptRepository = scriptRepository;
        this.userRepository = userRepository;
        this.bugAssemblyParserFactory = bugAssemblyParserFactory;
    }

    public List<Script> getAllValidScripts() {
        return scriptRepository.findByIsBytecodeValidTrue();
    }

    public List<Script> getUserScripts(String username) throws ResourceNotFoundException {
        User user = getUser(username);

        return scriptRepository.findByUser(user);
    }

    public Script getScript(long id, String username) throws ResourceNotFoundException, AccessDeniedException {
        User user = getUser(username);

        Script script = scriptRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Script not found."));

        if (!user.getId().equals(script.getUser().getId())) {
            throw new AccessDeniedException("Only the associated user can access this resource.");
        }

        return script;
    }

    public Script createScript(String scriptName, String scriptRaw, String username)
            throws ResourceNotFoundException, ResourceConflictException, ResourceValidationException {
        ProfanityFilter profanityFilter = new ProfanityFilter();
        if (profanityFilter.test("en", scriptName)) {
            throw new ResourceValidationException("Inappropriate language.");
        }

        Script script = new Script();
        User user = getUser(username);
        BugAssemblyParser parser = bugAssemblyParserFactory.createInstance();

        if (scriptRepository.existsByNameIgnoreCase(scriptName)) {
            throw new ResourceConflictException("Script by this name already exists");
        }

        List<Integer> byteCode;
        try {
            byteCode = parser.parse(scriptRaw);
            script.setBytecodeValid(true);
        } catch (BugAssemblyParseException e) {
            byteCode = new ArrayList<>();
            script.setBytecodeValid(false);
        }

        script.setUser(user);
        script.setName(scriptName);
        script.setRaw(scriptRaw);
        script.setBytecode(String.format("[%s]", byteCode.stream().map(Object::toString).collect(Collectors.joining(", "))));

        return scriptRepository.save(script);
    }

    public Script updateScript(long scriptId, String scriptName, String scriptRaw, String username)
            throws ResourceNotFoundException, ResourceValidationException, AccessDeniedException {
        ProfanityFilter profanityFilter = new ProfanityFilter();
        if (profanityFilter.test("en", scriptName)) {
            throw new ResourceValidationException("Inappropriate language.");
        }

        User user = getUser(username);
        Optional<Script> existingScript = scriptRepository.findById(scriptId);
        BugAssemblyParser parser = bugAssemblyParserFactory.createInstance();

        if (existingScript.isEmpty()) {
            throw new ResourceNotFoundException("Script not found.");
        }
        if (!existingScript.get().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This user does not have access to this script.");
        }

        Script scriptToSave = existingScript.get();
        List<Integer> byteCode;
        try {
            byteCode = parser.parse(scriptRaw);
            scriptToSave.setBytecodeValid(true);
        } catch (BugAssemblyParseException e) {
            byteCode = new ArrayList<>();
            scriptToSave.setBytecodeValid(false);
        }
        scriptToSave.setName(scriptName);
        scriptToSave.setRaw(scriptRaw);
        scriptToSave.setBytecode(
                String.format("[%s]", byteCode.stream().map(Object::toString).collect(Collectors.joining(", ")))
        );
        return scriptRepository.save(scriptToSave);
    }

    public void deleteScriptById(long scriptId, String username) throws ResourceNotFoundException, AccessDeniedException {
        User user = getUser(username);
        Optional<Script> existingScript = scriptRepository.findById(scriptId);

        if (existingScript.isEmpty()) {
            return;
        }

        if (!existingScript.get().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("This user does not have permission to delete this script.");
        }
        scriptRepository.deleteById(scriptId);
    }

    private User getUser(String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found."));
    }

}
