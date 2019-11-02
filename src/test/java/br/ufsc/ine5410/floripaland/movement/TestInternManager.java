package br.ufsc.ine5410.floripaland.movement;

import br.ufsc.ine5410.floripaland.safety.SafetyItem;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class TestInternManager extends InternManager {
    private AtomicInteger active = new AtomicInteger(0);

    public TestInternManager(int interns) {
        super(interns);
    }

    public boolean hasActiveIntern() {
        return active.get() > 0;
    }

    @Override
    public void transport(@Nonnull Collection<SafetyItem> items, @Nonnull Point from, @Nonnull Point to) {
        active.incrementAndGet();
        super.transport(items, from, to);
        active.decrementAndGet();
    }
}
