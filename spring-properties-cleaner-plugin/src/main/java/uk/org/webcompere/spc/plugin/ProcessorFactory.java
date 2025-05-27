package uk.org.webcompere.spc.plugin;

import uk.org.webcompere.spc.processor.Processor;

import java.util.function.Consumer;

@FunctionalInterface
public interface ProcessorFactory {
    /**
     * Create spring properties cleaner processor
     * @param errorLog error logger
     * @param infoLog info logger
     * @return new {@link Processor}
     */
    Processor create(Consumer<String> errorLog, Consumer<String> infoLog);
}
