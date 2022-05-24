package ch.uzh.ifi.hase.soprafs22.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



@RestController
public class SEEController {
    private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    @RestController
    @RequestMapping("/sse/notification")
    public class FolderWatchController implements AuditTrailListener<FolderChangeEvent> {

        private final FolderWatchService folderWatchService;

        FolderWatchController(FolderWatchService folderWatchService) {
            this.folderWatchService = folderWatchService;
        }

        private final SubscribableChannel subscribableChannel = MessageChannels.publishSubscribe().get();

        @PostConstruct
        void init() {
            folderWatchService.start(System.getProperty("user.home"));
        }

        @GetMapping(path = "/folder-watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        Flux<FolderChangeEvent.Event> getFolderWatch() {
            return Flux.create(sink -> {
                MessageHandler handler = message -> sink.next(FolderChangeEvent.class.cast(message.getPayload()).getEvent());
                sink.onCancel(() -> subscribableChannel.unsubscribe(handler));
                subscribableChannel.subscribe(handler);
            }, FluxSink.OverflowStrategy.LATEST);
        }

        @Override
        public void onApplicationEvent(FolderChangeEvent event) {
            subscribableChannel.send(new GenericMessage<>(event));
        }
    }


    @PostConstruct
    public void init() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                LOGGER.error(e.toString());
            }
        }));
    }

    @GetMapping("/time/{userId}")
    @CrossOrigin
    public SseEmitter streamDateTime( @PathVariable(value = "userId") long userId) {

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

        sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));

        executor.execute(() -> {
            for (int i = 0; i < 15; i++) {
                try {
                    sseEmitter.send(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss"))+" Id: "+userId);
                    sleep(1, sseEmitter);
                }
                catch (IOException e) {
                    e.printStackTrace();
                    sseEmitter.completeWithError(e);
                }
            }
            sseEmitter.complete();
        });

        LOGGER.info("Controller exits");
        return sseEmitter;
    }

    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            sseEmitter.completeWithError(e);
        }
    }
}

