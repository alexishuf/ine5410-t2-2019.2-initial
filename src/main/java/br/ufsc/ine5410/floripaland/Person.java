package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.safety.SafetyItem;

import javax.annotation.Nonnull;

public interface Person {
    boolean isPremium(@Nonnull Attraction.Type attractionType);
    void exitQueue(@Nonnull Attraction attraction);
    void notifyEnterAttraction(@Nonnull Attraction attraction);
    void notifyExitAttraction(@Nonnull Attraction attraction);
    void wear(@Nonnull SafetyItem item);
    void takeOff(@Nonnull SafetyItem item);
}
