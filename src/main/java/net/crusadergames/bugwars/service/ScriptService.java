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
        Long userId = getUserId(principal);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Logged in user does not exist.");
        }
        return scriptRepository.findByUser(user.get());
    }

    private Long getUserId(Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Logged in user does not exist.");
        }
        return user.get().getId();
    }


}
