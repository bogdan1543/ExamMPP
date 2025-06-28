package exam.rest.services;

import exam.model.Game;
import exam.persistence.hibernate.GameHibernateRepository;
import exam.services.AppException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameHibernateRepository gameRepository;


    @GetMapping("/{alias}")
    public ResponseEntity<?> getAllByAlias(@PathVariable String alias) {
        return ResponseEntity.ok(gameRepository.findByAlias(alias));
    }
    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String userError(AppException e) {
        return e.getMessage();
    }
}
