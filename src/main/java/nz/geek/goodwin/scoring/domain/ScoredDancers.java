package nz.geek.goodwin.scoring.domain;

import java.util.List;

public record ScoredDancers(Integer bibNumber, List<Person> dancers) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ScoredDancers that = (ScoredDancers) o;
        return dancers.equals(that.dancers);
    }

    @Override
    public int hashCode() {
        return dancers.hashCode();
    }

    @Override
    public String toString() {
        return "#" + bibNumber + " - " + dancers.stream().map(Person::fullName).reduce((a, b) -> a + ", " + b).orElse("");
    }
}
