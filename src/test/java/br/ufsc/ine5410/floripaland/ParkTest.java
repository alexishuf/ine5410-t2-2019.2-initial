package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.TimedVisitor;
import br.ufsc.ine5410.floripaland.movement.InternManager;
import br.ufsc.ine5410.floripaland.movement.Point;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static br.ufsc.ine5410.floripaland.Attraction.Type.FERRIS_WHEEL;
import static org.junit.Assert.*;

public class ParkTest {
    @Test
    public void testCreateAndClose() throws InterruptedException {
        InternManager interMgr = new InternManager(1);
        TimedVisitor visit = new TimedVisitor();
        Park park = new Park(interMgr);
        Attraction attraction = park.create(FERRIS_WHEEL, new Point(23, 5),
                1, 5, visit);
        assertNotNull(attraction);
        assertFalse(attraction.isOpen());
        attraction.openAttraction();
        assertTrue(attraction.isOpen());
        attraction.closeAttraction();
        park.close();
    }


    @Test
    public void testParkClosesAllAttractions() throws InterruptedException {
        InternManager interMgr = new InternManager(1);
        TimedVisitor visit = new TimedVisitor();
        Park park = new Park(interMgr);
        List<Attraction> list = new ArrayList<>();

        // cria 4 atrações
        for (int i = 0; i < 4; i++) {
            list.add(park.create(FERRIS_WHEEL, new Point(23, 5),
                    1, 5, visit));
        }

        // nenhum create retornou null
        Assert.assertEquals(0, list.stream().filter(Objects::isNull).count());

        // abre todas as atrações
        list.forEach(Attraction::openAttraction);
        Assert.assertEquals(list.size(), list.stream().filter(Attraction::isOpen).count());

        // fechar o parque deve fechar todas as atrações
        park.close();
        Assert.assertEquals(0, list.stream().filter(Attraction::isOpen).count());
    }
}