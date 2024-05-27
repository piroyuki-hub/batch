package com.example.batch.task;

import com.example.batch.model.Echo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class EchoItemProcessor implements ItemProcessor<Echo, Echo> {

    private static final Logger log = LoggerFactory.getLogger(EchoItemProcessor.class);

    @Override
    public Echo process(final Echo echo) {
        final var response = String.join("", echo.response(), "!!");
        final var transformEcho = new Echo(echo.call(), response);
        log.info("Converting ({}) info ({})", echo, transformEcho);
        return transformEcho;
    }
}
