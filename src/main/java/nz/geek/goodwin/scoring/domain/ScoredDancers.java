package nz.geek.goodwin.scoring.domain;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public record ScoredDancers(Integer bibNumber, List<Person> dancers) {
    public static ScoredDancers of(Integer number, String... names) {
        return new ScoredDancers(number, Stream.of(names).map(name -> new Person(UUID.randomUUID().toString(), name)).toList());
    }

    @Override
    public String toString() {
        return "#" + bibNumber + " - " + dancers.stream().map(Person::fullName).reduce((a, b) -> a + ", " + b).orElse("");
    }
}
