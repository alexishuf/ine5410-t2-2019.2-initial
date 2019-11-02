package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.MockPerson;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static br.ufsc.ine5410.floripaland.Attraction.Type.FERRIS_WHEEL;
import static org.junit.Assert.*;

public class AttractionQueueingTest extends AttractionTestBase {
    @Test
    public void testGroupSize() {
        attraction = build().withGroupSize(2).getOpen(FERRIS_WHEEL);
        MockPerson bob = new MockPerson(false), alice = new MockPerson(false);

        attraction.enter(alice);
        assertFalse(alice.waitForEnterAttraction(200));
        assertEquals(0, alice.getExitCalls());
        assertEquals(0, alice.getExitQueueCalls());

        attraction.enter(bob);
        assertTrue(bob.waitForEnterAttraction(200));
        assertTrue(alice.waitForEnterAttraction(200));
        assertTrue(bob.waitForExitAttraction(100));
        assertTrue(alice.waitForExitAttraction(100));

        assertEquals(0, alice.getExitQueueCalls());
        assertEquals(0, bob.getExitQueueCalls());
    }

    @Test
    public void testGroupCapacity() {
        attraction = build().withGroupCapacity(2).withTime(400).getOpen(FERRIS_WHEEL);
        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            personList.add(new MockPerson(false));
            attraction.enter(personList.get(i));
        }

        assertTrue(personList.get(1).waitForEnterAttraction(100));
        assertEquals(1, personList.get(0).getEnterCalls());
        assertFalse(personList.get(2).waitForEnterAttraction(100));
        assertTrue(personList.get(2).waitForEnterAttraction(400));
        assertTrue(personList.get(2).waitForExitAttraction(500));
        for (MockPerson person : personList)
            assertEquals("person=" + person, 0, person.getExitQueueCalls());
    }

    @Test
    public void testPremiumHasPriority() {
        attraction = build().withGroupCapacity(1).withTime(200).getOpen(FERRIS_WHEEL);
        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            personList.add(new MockPerson(i > 2));
            attraction.enter(personList.get(i));
        }
        assertTrue(personList.get(3).waitForEnterAttraction(200+100));
        assertFalse(personList.get(2).waitForEnterAttraction(200*2-100));
        assertTrue(personList.get(2).waitForEnterAttraction(200*3+100));
        assertTrue(personList.get(2).waitForExitAttraction(200+100));

        for (MockPerson person : personList) {
            String msg = "person=" + person;
            assertEquals(msg, 1, person.getEnterCalls());
            assertEquals(msg, 1, person.getExitCalls());
            assertEquals(msg, 0, person.getExitQueueCalls());
        }
    }

    @Test
    public void testExpelledFromQueue() throws InterruptedException {
        attraction = build().withGroupCapacity(1).withTime(300).getOpen(FERRIS_WHEEL);
        MockPerson alice = new MockPerson(), bob = new MockPerson();
        assertTrue(attraction.enter(alice));
        assertTrue(attraction.enter(bob));
        assertTrue(alice.waitForEnterAttraction(100));
        assertFalse(bob.waitForEnterAttraction(100));
        attraction.closeAttraction();
        assertEquals(1, alice.getExitCalls());
        assertEquals(1, bob.getExitQueueCalls());
    }

    public void stressTest(Builder builder, int groupSize) {
        attraction = builder.withTime(1, 2).withGroupSize(groupSize).getOpen(FERRIS_WHEEL);
        List<MockPerson> list = new ArrayList<>(2000*groupSize);
        for (int i = 0; i < 2000; i++) {
            list.add(new MockPerson(i % 5 == 0));
            attraction.enter(list.get(i));
        }
        for (MockPerson person : list)
            person.waitForExitAttraction(Integer.MAX_VALUE);
        assertEquals(0, list.stream().filter(p -> p.getEnterCalls() != 1).count());
        assertEquals(0, list.stream().filter(p -> p.getExitCalls() != 1).count());
        assertEquals(0, list.stream().filter(p -> p.getExitQueueCalls() > 0).count());
    }

    @Test(timeout = 2000*10)
    public void testStressSingletonsNoSafetyItems() {
        stressTest(build(), 1);
    }
    @Test(timeout = 2000*2*10)
    public void testStressPairsNoSafetyItems() {
        stressTest(build(), 2);
    }
}
