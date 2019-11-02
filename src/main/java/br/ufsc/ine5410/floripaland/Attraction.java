package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.movement.Point;
import br.ufsc.ine5410.floripaland.safety.SafetyItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface Attraction {
    enum Type {
        RAFTING,
        RAPPELLING,
        BUNGEE_JUMP,
        BANANA_BOAT,
        FERRIS_WHEEL,
        HIKING;

        public List<SafetyItem.Type> getRequiredSafetyItems() {
            List<SafetyItem.Type> list = new ArrayList<>();
            switch (this) {
                case FERRIS_WHEEL:
                case HIKING:
                    return list;
                case RAPPELLING:
                case BUNGEE_JUMP:
                    list.add(SafetyItem.Type.HELMET);
                    return list;
                case BANANA_BOAT:
                    list.add(SafetyItem.Type.LIFE_JACKET);
                    return list;
                case RAFTING:
                    list.add(SafetyItem.Type.HELMET);
                    list.add(SafetyItem.Type.LIFE_JACKET);
                    return list;
            }
            throw new UnsupportedOperationException("Unknown type " + this);
        }
    }

    @Nonnull Type getType();
    @Nonnull Point getPosition();
    boolean enter(@Nonnull Person person);
    void openAttraction();
    void closeAttraction() throws InterruptedException;
    boolean isOpen();
    void deliver(@Nonnull Collection<SafetyItem> collection);
}
