package br.ufsc.ine5410.floripaland.mocks;

import br.ufsc.ine5410.floripaland.Attraction;
import br.ufsc.ine5410.floripaland.Person;
import br.ufsc.ine5410.floripaland.safety.SafetyItem;
import com.google.common.base.Stopwatch;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static java.lang.String.format;

public class MockPerson implements Person {
    private static AtomicInteger nextId = new AtomicInteger(1);

    private boolean premium;
    private int id;
    private int exitQueueCalls = 0, enterCalls = 0, exitCalls = 0;
    private @Nonnull Set<SafetyItem.Type> requiredItems = new HashSet<>();
    private @Nonnull Set<SafetyItem> wearing = new HashSet<>();
    private @Nonnull List<String> wearErrors = new ArrayList<>();

    public MockPerson() {
        this(false);
    }

    public MockPerson(boolean premium) {
        this.id = nextId.getAndIncrement();
        this.premium = premium;
    }

    public synchronized @Nonnull
    MockPerson withRequiredItems(SafetyItem.Type... items) {
        return withRequiredItems(Arrays.asList(items));
    }

    public synchronized @Nonnull
    MockPerson withRequiredItems(Collection<SafetyItem.Type> types) {
        requiredItems.clear();
        requiredItems.addAll(types);
        return this;
    }

    @Override
    public boolean isPremium(@Nonnull Attraction.Type attractionType) {
        return premium;
    }

    @Override
    public synchronized void exitQueue(@Nonnull Attraction attraction) {
        ++exitQueueCalls;
        notifyAll();
    }

    @Override
    public synchronized void notifyEnterAttraction(@Nonnull Attraction a) {
        ++enterCalls;
        for (SafetyItem.Type type : requiredItems) {
            long count = wearing.stream().filter(i -> i.getType() == type).count();
            if (count == 0)
                wearErrors.add(format("%s entered %s not wearing a %s", this, a, type));
            if (count > 1)
                wearErrors.add(format("%s entered %s wearing %d %ss", this, a, count, type));
        }
        notifyAll();
    }

    @Override
    public synchronized void notifyExitAttraction(@Nonnull Attraction attraction) {
        ++exitCalls;
        notifyAll();
    }

    public int getEnterCalls() {
        return enterCalls;
    }

    public int getExitCalls() {
        return exitCalls;
    }

    public int getExitQueueCalls() {
        return exitQueueCalls;
    }

    private synchronized boolean waitForCall(int milliseconds, int number,
                                             @Nonnull Supplier<Integer> counter) {
        Stopwatch sw = Stopwatch.createStarted();
        while (counter.get() < number) {
            long available = milliseconds - sw.elapsed(TimeUnit.MILLISECONDS);
            try {
                if (available <= 0)
                    return false;
                wait(available);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return true;
    }

    public boolean waitForExitAttraction(int milliseconds) {
        return waitForExitAttraction(milliseconds, 1);
    }
    public boolean waitForExitAttraction(int milliseconds, int number) {
        return waitForCall(milliseconds, number, () -> exitCalls);
    }
    public boolean waitForEnterAttraction(int milliseconds) {
        return waitForEnterAttraction(milliseconds, 1);
    }
    public boolean waitForEnterAttraction(int milliseconds, int number) {
        return waitForCall(milliseconds, number, () -> enterCalls);
    }
    public boolean waitForExitQueue(int milliseconds) {
        return waitForExitQueue(milliseconds, 1);
    }
    public boolean waitForExitQueue(int milliseconds, int number) {
        return waitForCall(milliseconds, number, () -> exitQueueCalls);
    }

    @Override
    public void wear(@Nonnull SafetyItem item) {
        if (item.isInUse() && !wearing.contains(item)) {
            wearErrors.add(format("Tried to wear(%s), already in use by another Person", item));
        } else if (!wearing.add(item)) {
            wearErrors.add(format("Tried to wear(%s), but is already wearing it", item));
        } else {
            item.use(this);
        }
    }

    @Override
    public void takeOff(@Nonnull SafetyItem item) {
        if (!wearing.remove(item)) {
            wearErrors.add(format("Tried to takeOff(%s), but is not wearing it", item));
        } else if (item.getPersonWearing() != null && item.getPersonWearing() != this) {
            wearErrors.add(format("%s tried to takeOff(%s), being used by %s",
                    this, item, item.getPersonWearing()));
        } else {
            item.takeOff();
        }
    }

    public int getId() {
        return id;
    }

    public @Nonnull Set<SafetyItem> getWearing() {
        return wearing;
    }

    public @Nonnull List<String> getWearErrors() {
        return wearErrors;
    }

    @Override
    public String toString() {
        return format("Person(%d)", id);
    }

    public synchronized void reset() {
        wearErrors.clear();
        wearing.clear();
        exitQueueCalls = 0;
        enterCalls = 0;
        exitCalls = 0;
    }
}
