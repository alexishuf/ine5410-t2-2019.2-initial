package br.ufsc.ine5410.floripaland.safety;

import br.ufsc.ine5410.floripaland.Person;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SafetyItem {
    private Type type;
    private long uses = 0;
    private Person person = null;

    public enum Type {
        HELMET,
        LIFE_JACKET
    }

    public SafetyItem(Type type) {
        this.type = type;
    }

    public @Nonnull Type getType() {
        return type;
    }

    /**
     * Retorna true se, e somente se, está sendo vestido por um {@link Person}.
     */
    public boolean isInUse() {
        return person != null;
    }

    /**
     * Retorna a pessoa que está vestindo o item, ou null se !isInUse()
     */
    public @Nullable Person getPersonWearing() {
        return person;
    }

    /**
     * Chamado por {@link Person}.wear().
     */
    public void use(@Nonnull Person person) {
        ++uses;
        this.person = person;
    }

    /**
     * Chamado por {@link Person}.takeOff()
     */
    public void takeOff() {
        person = null;
    }

    /**
     * Número total de vezes que o item foi vestido.
     */
    public long getUses() {
        return uses;
    }
}
