package net.crusadergames.bugwars.service;

import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.model.auth.User;
import net.crusadergames.bugwars.repository.ScriptRepository;
import net.crusadergames.bugwars.repository.auth.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class ScriptService {

    @Autowired
    ScriptRepository scriptRepository;

    @Autowired
    UserRepository userRepository;

    public List<Script> getUserScripts(Principal principal) {
        User user = getUser(principal);

        return scriptRepository.findByUser(user);
    }

    public Script getByIdAndUser(long id, Principal principal) {
        User user = getUser(principal);

        Script script = scriptRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Script does not exist for user."));

        if (user != script.getUser()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the associated user can access this resource.");
        }

        return script;
    }

    private User getUser(Principal principal) {
        return userRepository.findByUsername(principal.getName()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "User does not exist."));
    }


}
