package nz.geek.goodwin.scoring.domain;

public record Judge(Person person, boolean chiefJudge) {
    @Override
    public String toString() {
        return person.fullName();
    }
}
