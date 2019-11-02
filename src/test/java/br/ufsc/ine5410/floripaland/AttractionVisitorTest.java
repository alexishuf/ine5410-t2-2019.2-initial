package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.MockPerson;
import org.junit.Test;

import static br.ufsc.ine5410.floripaland.Attraction.Type.FERRIS_WHEEL;
import static org.junit.Assert.*;

public class AttractionVisitorTest extends AttractionTestBase {
    @Test
    public void testEnterAndExitNotifications() {
        attraction = build().getOpen(FERRIS_WHEEL);
        MockPerson person = new MockPerson(false);
        attraction.enter(person);
        assertTrue(person.waitForEnterAttraction(200));
        assertTrue(person.waitForExitAttraction(200));
        assertEquals(0, person.getExitQueueCalls());
    }

    @Test
    public void testAttractionVisitTime() {
        attraction = build().withTime(400).getOpen(FERRIS_WHEEL);
        MockPerson person = new MockPerson(false);
        attraction.enter(person);
        assertTrue(person.waitForEnterAttraction(100));
        assertFalse(person.waitForExitAttraction(100));
        assertTrue(person.waitForExitAttraction(400));
        assertEquals(0, person.getExitQueueCalls());
    }
}
