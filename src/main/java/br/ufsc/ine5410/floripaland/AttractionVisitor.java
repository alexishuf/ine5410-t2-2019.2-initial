package br.ufsc.ine5410.floripaland;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Consumer;

public interface AttractionVisitor extends AutoCloseable {
    void visit(@Nonnull Collection<Person> groupWearingSafetyItems,
               @Nonnull Consumer<Collection<Person>> exitCallback);

    @Override
    void close();
}
