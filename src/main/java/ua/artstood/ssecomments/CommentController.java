package ua.artstood.ssecomments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RestController
public class CommentController {
    private SseEmitter sseEmitter;
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @PostConstruct
    public void init(){
        sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitter.onCompletion(()-> log.info("SseEmitter is completed"));
        sseEmitter.onTimeout(()-> log.info("SseEmitter is timed out"));
        sseEmitter.onError((ex)-> {
            log.info("Something went wrong");
            ex.printStackTrace();
        });
    }

    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribeForComments(){
        return new ResponseEntity<>(sseEmitter, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createMessage(@RequestBody Comment comment){
        try {
            sseEmitter.send(comment);
            return new ResponseEntity<>(
                    "Comment from user " +
                            comment.getUser() +
                            " was sent",
                    HttpStatus.OK);
        } catch (IOException e) {
            sseEmitter.completeWithError(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
