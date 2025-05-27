package uk.org.webcompere.spc.plugin;

import java.util.function.Consumer;
import uk.org.webcompere.spc.processor.Processor;

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
