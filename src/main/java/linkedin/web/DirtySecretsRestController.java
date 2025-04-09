package linkedin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/{id}")
    public DirtySecret getById(@PathVariable("id") String id) {
        return this.repository.getById(id).orElseThrow();
    }
    
    @PostMapping
    public DirtySecret post(@RequestBody DirtySecret secret) {
        return this.repository.save(secret);
    }
}
