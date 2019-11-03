package br.ufsc.ine5410.floripaland.movement;

import br.ufsc.ine5410.floripaland.safety.SafetyItem;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import static java.lang.Math.*;

public class InternManager {
    private Semaphore idleInterns;
    private final int hiredInterns;

    public InternManager(int interns) {
        this.hiredInterns = interns;
        this.idleInterns = new Semaphore(interns);
    }

    public void transport(@Nonnull Collection<SafetyItem> items,
                          @Nonnull Point from, @Nonnull Point to) {
        idleInterns.acquireUninterruptibly();
        try {
            Thread.sleep((int)floor(from.distanceTo(to)));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // preserva estado de interrompida
        } finally {
            idleInterns.release();
        }
    }

    public int getHiredInterns() {
        return hiredInterns;
    }
}
