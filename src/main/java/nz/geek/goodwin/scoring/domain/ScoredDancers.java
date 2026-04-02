package nz.geek.goodwin.scoring.domain;

import java.util.List;

public record ScoredDancers(Integer bibNumber, List<Person> dancers) {
    public String displayId() {
        return dancers.stream().map(Person::id).reduce((a, b) -> a + "/" + b).orElse("");
    }

    public String displayName() {
        return dancers.stream().map(Person::fullName).reduce((a, b) -> a + ", " + b).orElse("");
    }

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
}
