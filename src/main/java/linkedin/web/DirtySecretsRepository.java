package linkedin.web;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

public interface DirtySecretsRepository extends CrudRepository<DirtySecret, UUID> {
  

}