package linkedin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/dirty-secrets")
public class DirtySecretsRestController {
 
    @Autowired
    private DirtySecretsRepository repository;

    @GetMapping("/count")
    public int count() {
        return this.repository.count();
    }

    @GetMapping("/e1/{id}")
    public DirtySecret getByIdE1(@PathVariable("id") String id) {
        return this.repository.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Secret not found"));
    }

    @GetMapping("/e2/{id}")
    public DirtySecret getByIdE2(@PathVariable("id") String id) {
        return this.repository.getById(id).orElseThrow(() -> new NoSecretFoundWebException());
    }

    @GetMapping("/e3/{id}")
    public DirtySecret getByIdE3(@PathVariable("id") String id) {
        return this.repository.getById(id).orElseThrow(() -> new NoSecretFoundException());
    }

    @ExceptionHandler(NoSecretFoundException.class)
    public ResponseEntity<String> handleNoSecretFoundException() {
        return ResponseEntity.internalServerError().body("Secret not found");
    }
    
    @PostMapping
    public DirtySecret post(@RequestBody DirtySecret secret) {
        return this.repository.save(secret);
    }
}
