package net.crusadergames.bugwars.controller;

import net.crusadergames.bugwars.dto.request.ModifyScriptDTO;
import net.crusadergames.bugwars.dto.response.ScriptNameDTO;
import net.crusadergames.bugwars.exception.AccessDeniedException;
import net.crusadergames.bugwars.exception.ResourceConflictException;
import net.crusadergames.bugwars.exception.ResourceNotFoundException;
import net.crusadergames.bugwars.exception.ResourceValidationException;
import net.crusadergames.bugwars.model.Script;
import net.crusadergames.bugwars.service.ScriptService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@CrossOrigin
@RequestMapping("api/scripts")
@PreAuthorize("hasRole('USER')")
@RestController
public class ScriptController {

    private final ScriptService scriptService;

    public ScriptController(ScriptService scriptService) {
        this.scriptService = scriptService;
    }


    @GetMapping("/all")
    @PreAuthorize("permitAll()")
    public List<ScriptNameDTO> getAllNamesOfValidScripts() {
        List<Script> validScripts = scriptService.getAllValidScripts();
        return validScripts.stream()
                .map((script) -> new ScriptNameDTO(script.getId(), script.getName(), script.getUser().getUsername()))
                .toList();
    }

    @GetMapping
    public List<Script> getUserScripts(Principal principal) {
        try {
            return scriptService.getUserScripts(principal.getName());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User does not exist.");
        }
    }

    @GetMapping(path = "/{id}")
    public Script getScript(@PathVariable long id, Principal principal) {
        try {
            return scriptService.getScript(id, principal.getName());
        } catch (ResourceNotFoundException e) {
            if (e.getMessage().equals("User not found.")) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public Script createScript(@RequestBody ModifyScriptDTO request, Principal principal) {
        try {
            return scriptService.createScript(request.getName(), request.getRaw(), principal.getName());
        } catch (ResourceValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (ResourceConflictException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    @PutMapping(path = "/{scriptId}")
    public Script updateScript(@PathVariable Long scriptId, @RequestBody ModifyScriptDTO request, Principal principal) {
        try {
            return scriptService.updateScript(scriptId, request.getName(), request.getName(), principal.getName());
        } catch (ResourceNotFoundException e) {
            if (e.getMessage().equals("User not found.")) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            }
        } catch (ResourceValidationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{scriptId}")
    public void deleteScriptById(@PathVariable long scriptId, Principal principal) {
        try {
            scriptService.deleteScriptById(scriptId, principal.getName());
        } catch (ResourceNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (AccessDeniedException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

}
