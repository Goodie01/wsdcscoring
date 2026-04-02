package nz.geek.goodwin.scoring.domain;

public record Judge(Person person, boolean chiefJudge) {
    public String displayName() {
        return person.fullName();
    }
}
