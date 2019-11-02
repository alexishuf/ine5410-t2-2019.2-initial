package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.MockPerson;
import br.ufsc.ine5410.floripaland.safety.SafetyItem;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static br.ufsc.ine5410.floripaland.Attraction.Type.*;
import static br.ufsc.ine5410.floripaland.safety.SafetyItem.Type.HELMET;
import static br.ufsc.ine5410.floripaland.safety.SafetyItem.Type.LIFE_JACKET;
import static org.junit.Assert.*;

public class SafetyItemsBalancerTest extends AttractionTestBase {
    @Test
    public void testTwoAttractionStealAll() {
        Builder builder = build().withTime(20);
        Attraction r = builder.withItem(HELMET, 0).getOpen(RAPPELLING);
        Attraction f = builder.withItem(HELMET, 3).getOpen(FERRIS_WHEEL);

        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            personList.add(new MockPerson());
            assertTrue("i="+i, f.enter(personList.get(i)));
        }
        assertTrue(personList.get(49).waitForExitAttraction((20*50)/3 + 100));
        checkPersonList(personList);

        personList.clear();
        for (int i = 0; i < 50; i++) {
            personList.add(new MockPerson().withRequiredItems(HELMET));
            assertTrue("i="+i, r.enter(personList.get(i)));
        }
        // r deve receber os capacetes em f
        assertTrue(personList.get(49).waitForExitAttraction((50*20) / 3 + 200));
        checkPersonList(personList);

        // há um ponto onde r pega todos os capacetes
        assertEquals(3, visitors.get(0).maxVisitors());
    }

    @Test
    public void testDistributeInEqualDemand() {
        Builder builder = build().withGroupCapacity(10).withTime(50);
        Attraction[] r = {
                builder.withItem(HELMET, 0).getOpen(RAPPELLING),
                builder.withItem(HELMET, 10).getOpen(RAPPELLING)
        };

        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            personList.add(new MockPerson().withRequiredItems(HELMET));
            assertTrue("i="+i, r[i%2].enter(personList.get(i)));
        }

        int timeout = (50 * 50) + 100;
        assertTrue(personList.get(49).waitForExitAttraction(timeout));
        assertTrue(personList.get(48).waitForExitAttraction(timeout));
        checkPersonList(personList);

        assertTrue(visitors.get(0).maxVisitors() >= 3);
        assertTrue(visitors.get(1).maxVisitors() >= 3);
    }

    @Test
    public void testDistributeInEqualDemandDifferentNeeds() {
        Builder builder = build().withGroupCapacity(10).withTime(50)
                .withItem(HELMET, 5).withItem(LIFE_JACKET, 5);
        Attraction[] r = {
                builder.withItem(HELMET, 5).getOpen(RAPPELLING),
                builder.withItem(HELMET, 5).getOpen(FERRIS_WHEEL),
                builder.withItem(HELMET, 5).getOpen(RAFTING),
                builder.withItem(HELMET, 5).getOpen(BANANA_BOAT)
        };

        List<MockPerson> personList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            List<SafetyItem.Type> required = r[i % 4].getType().getRequiredSafetyItems();
            personList.add(new MockPerson().withRequiredItems(required));
            assertTrue("i="+i, r[i%4].enter(personList.get(i)));
        }

        int timeout = (100 * 50) + 100;
        for (int i = 96; i < 100; i++)
            assertTrue("i="+i, personList.get(i).waitForExitAttraction(timeout));
        checkPersonList(personList);

        for (int i = 0; i < 4; i++)
            assertTrue("i=" + i, visitors.get(i).maxVisitors() >= 3);
    }

    private void checkPersonList(List<MockPerson> personList) {
        assertEquals(0, personList.stream().filter(p -> p.getEnterCalls() == 0).count());
        assertEquals(0, personList.stream().filter(p -> p.getEnterCalls() > 1).count());
        assertEquals(0, personList.stream().filter(p -> p.getExitCalls() == 0).count());
        assertEquals(0, personList.stream().filter(p -> p.getExitCalls() > 1).count());

        List<String> errors = personList.stream().flatMap(p -> p.getWearErrors().stream())
                .collect(Collectors.toList());
        assertEquals(Collections.emptyList(), errors);
    }


}
