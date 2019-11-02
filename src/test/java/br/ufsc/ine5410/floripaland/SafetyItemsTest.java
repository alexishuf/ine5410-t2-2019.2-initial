package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.MockPerson;
import br.ufsc.ine5410.floripaland.safety.SafetyItem;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.util.*;

import static br.ufsc.ine5410.floripaland.Attraction.Type.RAFTING;
import static br.ufsc.ine5410.floripaland.Attraction.Type.RAPPELLING;
import static br.ufsc.ine5410.floripaland.safety.SafetyItem.Type.HELMET;
import static br.ufsc.ine5410.floripaland.safety.SafetyItem.Type.LIFE_JACKET;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class SafetyItemsTest extends AttractionTestBase {

    @Test
    public void testAttractionWithoutHelmets() throws InterruptedException {
        attraction = build().withItem(HELMET, 0).getOpen(RAPPELLING);
        MockPerson person = new MockPerson(false);
        assertTrue(attraction.enter(person));
        assertFalse(person.waitForEnterAttraction(100));
        attraction.closeAttraction();
        assertEquals(1, person.getExitQueueCalls());
    }

    @Test
    public void testWaitsAndReturnHelmet() {
        attraction = build().withItem(HELMET, 1).withTime(200).getOpen(RAPPELLING);
        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            personList.add(new MockPerson(false));
            assertTrue("i="+i, attraction.enter(personList.get(i)));
        }
        assertTrue(personList.get(0).waitForEnterAttraction(100));
        assertFalse(personList.get(1).waitForEnterAttraction(100));
        assertTrue(personList.get(1).waitForEnterAttraction(100+100));
        assertEquals(1, personList.get(0).getExitCalls());
        assertTrue(personList.get(1).waitForExitAttraction(200+100));
    }

    @Test
    public void testReceivesHelmetLate() {
        attraction = build().withItem(HELMET, 0).withTime(200).getOpen(RAPPELLING);
        MockPerson alice = new MockPerson();
        assertTrue(attraction.enter(alice));
        assertFalse(alice.waitForEnterAttraction(100));
        attraction.deliver(Collections.singletonList(new SafetyItem(HELMET)));
        assertTrue(alice.waitForEnterAttraction(100));
        assertTrue(alice.waitForExitAttraction(200+100));
        assertEquals(0, alice.getExitQueueCalls());
    }

    @Test
    public void testReceivesExtraHelmet() {
        attraction = build().withItem(HELMET, 1).withTime(200).getOpen(RAPPELLING);
        MockPerson alice = new MockPerson(), bob = new MockPerson();
        assertTrue(attraction.enter(alice));
        assertTrue(attraction.enter(bob));
        assertTrue(alice.waitForEnterAttraction(100));
        assertFalse(bob.waitForEnterAttraction(100));
        attraction.deliver(Collections.singletonList(new SafetyItem(HELMET)));
        assertTrue(bob.waitForEnterAttraction(100));
        assertTrue(alice.waitForExitAttraction(100+100));
        assertTrue(bob.waitForExitAttraction(100+100));
        assertEquals(0, alice.getExitQueueCalls());
    }

    @Test
    public void testPersonReallyWearsHelmet() {
        attraction = build().withItem(HELMET, 1).withTime(200).getOpen(RAPPELLING);
        MockPerson alice = new MockPerson();
        assertTrue(attraction.enter(alice));
        assertTrue(alice.waitForEnterAttraction(100));

        Set<SafetyItem> wearing = alice.getWearing();
        assertEquals(1, wearing.size());
        SafetyItem helmet = wearing.iterator().next();
        assertEquals(HELMET, helmet.getType());
        assertTrue(helmet.isInUse());
        assertEquals(alice, helmet.getPersonWearing());

        assertTrue(alice.waitForExitAttraction(200+100));

        assertFalse(helmet.isInUse());
        assertNull(helmet.getPersonWearing());
        assertEquals(Collections.emptySet(), alice.getWearing());
        assertEquals(Collections.emptyList(), alice.getWearErrors());
    }

    @Test
    public void testHelmetIsReturned() {
        attraction = build().withItem(HELMET, 1).withTime(200).getOpen(RAPPELLING);
        MockPerson alice = new MockPerson(), bob = new MockPerson();
        assertTrue(attraction.enter(alice));
        assertTrue(attraction.enter(bob));
        assertTrue(alice.waitForEnterAttraction(100));

        Set<SafetyItem> wearing = new HashSet<>(alice.getWearing());
        assertEquals(1, wearing.size());
        SafetyItem helmet = wearing.iterator().next();
        assertEquals(HELMET, helmet.getType());

        assertTrue(bob.waitForEnterAttraction(200+100));
        assertEquals(wearing, bob.getWearing());

        assertTrue(bob.waitForExitAttraction(200+100));

        assertEquals(Collections.emptyList(), alice.getWearErrors());
        assertEquals(Collections.emptyList(), bob.getWearErrors());
    }

    public void queueProcessTest(@Nonnull Builder builder, int queueSize, int maxVisitors,
                                 @Nonnull Attraction.Type type) {
        attraction = builder.getOpen(type);
        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < queueSize; i++) {
            personList.add(new MockPerson().withRequiredItems(builder.getRequiredItems()));
            assertTrue("i="+i, attraction.enter(personList.get(i)));
        }
        assertTrue(personList.get(queueSize-1)
                .waitForExitAttraction(builder.getMaxTime()*queueSize+100));

        assertEquals(maxVisitors, visitor.maxVisitors());
        List<String> errors = personList.stream()
                .flatMap(p -> p.getWearErrors().stream()).collect(toList());
        assertEquals(Collections.emptyList(), errors);
    }

    @Test
    public void testTwoItemsRequired() {
        queueProcessTest(build().withItem(LIFE_JACKET, 1)
                .withItem(HELMET, 1).withTime(100), 3, 1, RAFTING);
    }

    @Test
    public void testTwoItemsRequiredUnbalanced() {
        queueProcessTest(build().withItem(LIFE_JACKET, 1)
                .withItem(HELMET, 2).withTime(100), 3, 1, RAFTING);
    }

    @Test
    public void testTwoItemsRequiredWithConcurrency() {
        queueProcessTest(build().withItem(LIFE_JACKET, 2)
                .withItem(HELMET, 2).withTime(100), 3, 2, RAFTING);
    }

    @Test
    public void testOneItemStress() {
        queueProcessTest(build().withItem(HELMET, 2)
                .withTime(1, 4), 2000, 2, RAPPELLING);
    }

    @Test
    public void testTwoItemStress() {
        queueProcessTest(build().withItem(LIFE_JACKET, 2)
                .withItem(HELMET, 2).withTime(1, 4), 2000, 2, RAFTING);
    }

    @Test
    public void testTwoItemUnbalancedStress() {
        queueProcessTest(build().withItem(LIFE_JACKET, 3)
                .withItem(HELMET, 2).withTime(1, 4), 2000, 2, RAFTING);
    }
}
