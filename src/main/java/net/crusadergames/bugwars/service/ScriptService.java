package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.dto.request.CreateScriptRequest;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.parser.BugAssemblyParseException;
import net.crusadergames.bugwars.parser.BugAssemblyParser;
import net.crusadergames.bugwars.parser.BugAssemblyParserFactory;
import net.crusadergames.bugwars.repository.ScriptRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScriptService {

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BugAssemblyParserFactory bugAssemblyParserFactory;

    public List<Script> getUserScripts(Principal principal) {
        User user = getUser(principal);

        return scriptRepository.findByUser(user);
    }

    public Script getScript(long id, Principal principal) {
        User user = getUser(principal);

        Script script = scriptRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Script does not exist."));

        if (!user.getId().equals(script.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the associated user can access this resource.");
        }

        return script;
    }

    public Script createScript(CreateScriptRequest request, Principal principal) {
        Script script = new Script();
        User user = getUser(principal);
        BugAssemblyParser parser = bugAssemblyParserFactory.createInstance();

        if (scriptRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Script by this name already exists");
        }

        List<Integer> byteCode;
        try {
            byteCode = parser.parse(request.getRaw());
            script.setBytecodeValid(true);
        } catch (BugAssemblyParseException e) {
            byteCode = new ArrayList<>();
            script.setBytecodeValid(false);
        }

        script.setUser(user);
        script.setName(request.getName());
        script.setRaw(request.getRaw());
        script.setBytecode(String.format("[%s]", byteCode.stream().map(Object::toString).collect(Collectors.joining(", "))));

        return scriptRepository.save(script);
    }

    public void deleteScriptById(long scriptId, Principal principal){
        User user = getUser(principal);
        Optional<Script> existingScript = scriptRepository.findById(scriptId);

        if(existingScript.isEmpty()){
            return;
        }

        if(!existingScript.get().getUser().getId().equals(user.getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This action cannot be done.");
        }
        scriptRepository.deleteById(scriptId);
    }

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "User does not exist."));
    }




}
