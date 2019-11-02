package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.TimedVisitor;
import br.ufsc.ine5410.floripaland.movement.Point;
import br.ufsc.ine5410.floripaland.movement.TestInternManager;
import br.ufsc.ine5410.floripaland.safety.SafetyItem;
import com.google.common.base.Preconditions;
import org.junit.After;

import javax.annotation.Nonnull;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AttractionTestBase {
    protected Attraction attraction;
    protected TimedVisitor visitor;
    protected List<TimedVisitor> visitors;
    protected TestInternManager internMgr;
    protected Park park;

    protected @Nonnull Builder build() {
        return new Builder();
    }

    @After
    public void tearDown() throws Exception {
        if (park != null) {
            park.close();
            // a atração está fechada
            assertFalse(attraction.isOpen());

            // não há nenhuma pessoa dentro da atração
            assertFalse(visitor.hasVisitor());

            // método close foi chamado
            assertTrue(visitor.getExecutor().isShutdown());
            assertTrue(visitor.getExecutor().isTerminated());

            // todos os estagiários estão livres
            assertFalse(internMgr.hasActiveIntern());
        }
    }

    protected class Builder {
        private @Nonnull Point nextPosition = new Point(0, 0);
        private int interns = 1;
        private int groupSize = 1;
        private int groupCapacity = 3;
        private int minTime = 1, maxTime = 1;
        private Map<SafetyItem.Type, Integer> itemCounts = new HashMap<>();
        private boolean parkBuilt = false;

        public @Nonnull AttractionTestBase.Builder withInterns(int interns) {
            Preconditions.checkState(!parkBuilt);
            this.interns = interns;
            return this;
        }
        public @Nonnull AttractionTestBase.Builder withGroupSize(int groupSize) {
            this.groupSize = groupSize;
            return this;
        }
        public @Nonnull AttractionTestBase.Builder withGroupCapacity(int groupCapacity) {
            this.groupCapacity = groupCapacity;
            return this;
        }
        public @Nonnull AttractionTestBase.Builder withTime(int time) {
            return withTime(time, time);
        }
        public @Nonnull AttractionTestBase.Builder withTime(int minTime, int maxTime) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            return this;
        }
        public @Nonnull AttractionTestBase.Builder withItem(@Nonnull SafetyItem.Type type, int count) {
            this.itemCounts.put(type, count);
            return this;
        }
        public @Nonnull Attraction get(@Nonnull Attraction.Type type) {
            if (!parkBuilt) {
                parkBuilt = true;
                internMgr = new TestInternManager(interns);
                park = new Park(internMgr);
                visitors = new ArrayList<>();
            }
            visitors.add(visitor = new TimedVisitor(minTime, maxTime));
            attraction = park.create(type, nextPosition, groupSize, groupCapacity, visitor);
            nextPosition = new Point(nextPosition.getX()+20, nextPosition.getY());
            for (Map.Entry<SafetyItem.Type, Integer> e : itemCounts.entrySet()) {
                List<SafetyItem> list = new ArrayList<>(e.getValue());
                for (int i = 0; i < e.getValue(); i++)
                    list.add(new SafetyItem(e.getKey()));
                attraction.deliver(list);
            }
            return attraction;
        }
        public @Nonnull Attraction getOpen(@Nonnull Attraction.Type type) {
            Attraction attraction = get(type);
            attraction.openAttraction();
            return attraction;
        }

        public Collection<SafetyItem.Type> getRequiredItems() {
            return itemCounts.keySet();
        }

        public int getMaxTime() {
            return maxTime;
        }
    }
}
