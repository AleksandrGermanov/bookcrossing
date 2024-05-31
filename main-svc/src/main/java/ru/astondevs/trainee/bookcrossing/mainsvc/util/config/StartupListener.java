package ru.astondevs.trainee.bookcrossing.mainsvc.util.config;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.astondevs.trainee.bookcrossing.mainsvc.util.logging.RequestAppender;

@Component
@RequiredArgsConstructor
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    private final ApplicationContext context;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Appender<ILoggingEvent> customAppender = context.getBean(RequestAppender.class);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        customAppender.setContext(loggerContext);
        customAppender.start();
        rootLogger.addAppender(customAppender);
    }
}
