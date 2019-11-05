package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.movement.InternManager;
import br.ufsc.ine5410.floripaland.movement.Point;

import javax.annotation.Nonnull;

public class Park implements AutoCloseable {
    public Park(@Nonnull InternManager internManager) {
    }

    public Attraction create(@Nonnull Attraction.Type type, @Nonnull Point position,
                             int groupSize,
                             int groupsCapacity, @Nonnull AttractionVisitor visit) {
        throw new UnsupportedOperationException("Me implemente!");
    }

    @Override
    public void close() throws InterruptedException {
        throw new UnsupportedOperationException("Me implemente!");
    }
}
