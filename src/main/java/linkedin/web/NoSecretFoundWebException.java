package linkedin.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Secret not found")
public class NoSecretFoundWebException extends RuntimeException {

}
