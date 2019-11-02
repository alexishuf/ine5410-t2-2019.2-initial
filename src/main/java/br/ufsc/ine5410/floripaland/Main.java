package br.ufsc.ine5410.floripaland;

import br.ufsc.ine5410.floripaland.mocks.MockPerson;
import br.ufsc.ine5410.floripaland.mocks.TimedVisitor;
import br.ufsc.ine5410.floripaland.movement.InternManager;
import br.ufsc.ine5410.floripaland.movement.Point;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static br.ufsc.ine5410.floripaland.Attraction.Type.*;

public class Main {
    private final @Nonnull List<TimedVisitor> visitors = new ArrayList<>();
    private final @Nonnull List<Attraction> attractions = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        /* Esse é o main() executado quando o programa é executado com java -jar.
         * Altere como desejar. Erros aqui não impactarão a avaliação */
        Main app = new Main();
        app.run();
    }

    private void run() throws Exception {
        try (Park park = new Park(new InternManager(2))) {
            setUpPark(park);
            generatePersons();
        }
    }

    private void generatePersons() throws IOException, InterruptedException {
        Thread generator = new Thread(() -> {
            while (true) {
                int idx = (int) Math.floor(Math.random() * attractions.size());
                Attraction a = attractions.get(idx);
                MockPerson person = new MockPerson(Math.random() > 0.8);
                boolean ok = a.enter(person);
                System.out.printf("%s %s %s@%s\n", person, ok ? "entered" : "gave up",
                        a.getType(), a.getPosition());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        generator.start();
        System.out.println("\n   ~~~ Hit RETURN to stop generating traffic ~~~");
        //noinspection ResultOfMethodCallIgnored
        System.in.read();
        generator.interrupt();
        generator.join();
    }

    private  void setUpPark(@Nonnull Park park) {
        Point point = new Point(0, 0), step = new Point(20, 0);
        TimedVisitor v400 = new TimedVisitor(400, 600);
        TimedVisitor v900 = new TimedVisitor(900, 1200);
        TimedVisitor v100 = new TimedVisitor(100, 300);

        visitors.add(v400);
        attractions.add(park.create(RAFTING, point, 4, 5, v400));
        point = point.plus(step);

        visitors.add(v400);
        attractions.add(park.create(RAPPELLING, point, 4, 5, v400));
        point = point.plus(step);

        visitors.add(v100);
        attractions.add(park.create(BUNGEE_JUMP, point, 4, 5, v100));
        point = point.plus(step);

        visitors.add(v400);
        attractions.add(park.create(BANANA_BOAT, point, 4, 5, v400));
        point = point.plus(step);

        visitors.add(v100);
        attractions.add(park.create(FERRIS_WHEEL, point, 4, 5, v100));
        point = point.plus(step);

        visitors.add(v900);
        attractions.add(park.create(HIKING, point, 4, 5, v900));

        for (Attraction a : attractions) {
            a.openAttraction();
        }
    }
}
