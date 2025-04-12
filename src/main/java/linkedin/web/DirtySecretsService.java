package linkedin.web;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class DirtySecretsService {
    
    @Autowired
    private DirtySecretsRepository dirtySecretsRepository;

    @Transactional
    public void deleteAll(List<UUID> ids) {
        for (UUID id : ids) {
            dirtySecretsRepository.deleteById(id);
        }
    }
}
