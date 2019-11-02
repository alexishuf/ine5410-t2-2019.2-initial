package br.ufsc.ine5410.floripaland.mocks;

import br.ufsc.ine5410.floripaland.AttractionVisitor;
import br.ufsc.ine5410.floripaland.Person;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TimedVisitor implements AttractionVisitor {
    private int minTime, maxTime;
    private @Nonnull ScheduledExecutorService executor;
    private @Nonnull AtomicInteger visiting = new AtomicInteger(0);
    private @Nonnull AtomicInteger maxVisiting = new AtomicInteger(0);

    public TimedVisitor(int minTime, int maxTime) {
        Preconditions.checkArgument(maxTime >= minTime);
        this.minTime = minTime;
        this.maxTime = maxTime;
        RejectedExecutionHandler rejection = new ThreadPoolExecutor.CallerRunsPolicy();
        executor = new ScheduledThreadPoolExecutor(0, rejection);
    }

    public TimedVisitor() {
        this(1, 1);
    }

    public void reset() {
        Preconditions.checkState(!hasVisitor());
        Preconditions.checkState(!executor.isShutdown());
        visiting.set(0);
        maxVisiting.set(0);
    }

    public @Nonnull ScheduledExecutorService getExecutor() {
        return executor;
    }

    public boolean hasVisitor() {
        return visiting.get() > 0;
    }

    public int maxVisitors() {
        return maxVisiting.get();
    }

    @Override
    public void visit(@Nonnull Collection<Person> groupWearingSafetyItems,
                      @Nonnull Consumer<Collection<Person>> exitCallback) {
        Stopwatch sw = Stopwatch.createStarted();
        int observed = visiting.incrementAndGet();
        for (int current = maxVisiting.get(); observed > current; current = maxVisiting.get()) {
            if (maxVisiting.compareAndExchange(current, observed) == current)
                break;
        }
        int duration = minTime + (int) Math.round(Math.random() * (maxTime - minTime));
        executor.schedule(() -> {
            visiting.decrementAndGet();
            exitCallback.accept(groupWearingSafetyItems);
        }, duration, MILLISECONDS);
    }

    @Override
    public void close() {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
