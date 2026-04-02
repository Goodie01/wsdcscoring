package nz.geek.goodwin.scoring;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.Person;
import nz.geek.goodwin.scoring.domain.RelativeScore;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.internal.Spreadsheet;
import nz.geek.goodwin.scoring.relative.RelativeScoringService;

import java.math.BigDecimal;
import java.util.*;

public class Main {
    static void main() {
        Judge judge1 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #1"), false);
        Judge judge2 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #2"), false);
        Judge judge3 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #3"), false);
        Judge judge4 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #4"), false);
        Judge judge5 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #5"), false);
        Judge judge6 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #6"), false);
        Judge judge7 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #7"), false);
        Judge judge8 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #8"), false);
        Judge judge9 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #9"), false);
        Judge judgeChief = new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true);

        ScoredDancers couple1 = buildScoredDancers(1, List.of("Romie", "Julie"));
        ScoredDancers couple2 = buildScoredDancers(2, List.of("Marc", "Cleo"));
        ScoredDancers couple3 = buildScoredDancers(3, List.of("George", "Gracie"));
        ScoredDancers couple4 = buildScoredDancers(4, List.of("Jack", "Annie"));
        ScoredDancers couple5 = buildScoredDancers(5, List.of("Rhett", "Scarlet"));
        ScoredDancers couple6 = buildScoredDancers(6, List.of("Rocky", "Adrian"));
        ScoredDancers couple7 = buildScoredDancers(7, List.of("Fred", "Ginger"));
        ScoredDancers couple8 = buildScoredDancers(8, List.of("Barney", "Betty"));
        ScoredDancers couple9 = buildScoredDancers(9, List.of("Ricky", "Lucy"));
        ScoredDancers couple10 = buildScoredDancers(10, List.of("Ken", "Barbie"));
        ScoredDancers couple11 = buildScoredDancers(11, List.of("Ike", "Mamie"));
        ScoredDancers couple12 = buildScoredDancers(12, List.of("Ward", "June"));

        Spreadsheet<ScoredDancers, Judge, String> spreadsheet = new Spreadsheet<>();
//        spreadsheet.addColumns(Set.of(judge1, judge2, judge3, judge4, judge5, judge6, judge7, judge8, judge9, judgeChief));
        spreadsheet.addColumns(Set.of(judge1, judge2, judge3, judge4, judge5, judge6, judge7, judgeChief));
        spreadsheet.addRows(Set.of(couple1, couple2, couple3, couple4, couple5, couple6, couple7, couple8, couple9, couple10, couple11, couple12));

        spreadsheet.put(couple1, judge1, "8.75");
        spreadsheet.put(couple2, judge1, "8.63");
        spreadsheet.put(couple3, judge1, "8.85");
        spreadsheet.put(couple4, judge1, "9.00");
        spreadsheet.put(couple5, judge1, "8.69");
        spreadsheet.put(couple6, judge1, "8.60");
        spreadsheet.put(couple7, judge1, "9.10");
        spreadsheet.put(couple8, judge1, "8.64");
        spreadsheet.put(couple9, judge1, "8.90");
        spreadsheet.put(couple10, judge1, "8.65");
        spreadsheet.put(couple11, judge1, "8.40");
        spreadsheet.put(couple12, judge1, "8.67");

        spreadsheet.put(couple1, judge2, "8.00");
        spreadsheet.put(couple2, judge2, "8.60");
        spreadsheet.put(couple3, judge2, "8.25");
        spreadsheet.put(couple4, judge2, "8.38");
        spreadsheet.put(couple5, judge2, "8.30");
        spreadsheet.put(couple6, judge2, "8.10");
        spreadsheet.put(couple7, judge2, "8.35");
        spreadsheet.put(couple8, judge2, "8.20");
        spreadsheet.put(couple9, judge2, "8.50");
        spreadsheet.put(couple10, judge2, "8.32");
        spreadsheet.put(couple11, judge2, "8.23");
        spreadsheet.put(couple12, judge2, "8.15");

        spreadsheet.put(couple1, judge3, "9.34");
        spreadsheet.put(couple2, judge3, "9.20");
        spreadsheet.put(couple3, judge3, "9.33");
        spreadsheet.put(couple4, judge3, "9.35");
        spreadsheet.put(couple5, judge3, "9.31");
        spreadsheet.put(couple6, judge3, "9.65");
        spreadsheet.put(couple7, judge3, "9.70");
        spreadsheet.put(couple8, judge3, "9.25");
        spreadsheet.put(couple9, judge3, "9.72");
        spreadsheet.put(couple10, judge3, "9.32");
        spreadsheet.put(couple11, judge3, "9.28");
        spreadsheet.put(couple12, judge3, "9.45");

        spreadsheet.put(couple1, judge4, "7.70");
        spreadsheet.put(couple2, judge4, "8.60");
        spreadsheet.put(couple3, judge4, "7.90");
        spreadsheet.put(couple4, judge4, "9.00");
        spreadsheet.put(couple5, judge4, "8.50");
        spreadsheet.put(couple6, judge4, "7.80");
        spreadsheet.put(couple7, judge4, "8.80");
        spreadsheet.put(couple8, judge4, "7.95");
        spreadsheet.put(couple9, judge4, "8.20");
        spreadsheet.put(couple10, judge4, "8.25");
        spreadsheet.put(couple11, judge4, "7.75");
        spreadsheet.put(couple12, judge4, "8.30");

        spreadsheet.put(couple1, judge5, "9.10");
        spreadsheet.put(couple2, judge5, "9.15");
        spreadsheet.put(couple3, judge5, "9.20");
        spreadsheet.put(couple4, judge5, "9.60");
        spreadsheet.put(couple5, judge5, "9.50");
        spreadsheet.put(couple6, judge5, "9.30");
        spreadsheet.put(couple7, judge5, "8.70");
        spreadsheet.put(couple8, judge5, "9.25");
        spreadsheet.put(couple9, judge5, "9.40");
        spreadsheet.put(couple10, judge5, "8.60");
        spreadsheet.put(couple11, judge5, "8.65");
        spreadsheet.put(couple12, judge5, "9.12");

        spreadsheet.put(couple1, judge6, "7.00");
        spreadsheet.put(couple2, judge6, "7.20");
        spreadsheet.put(couple3, judge6, "7.60");
        spreadsheet.put(couple4, judge6, "8.50");
        spreadsheet.put(couple5, judge6, "7.24");
        spreadsheet.put(couple6, judge6, "7.10");
        spreadsheet.put(couple7, judge6, "7.15");
        spreadsheet.put(couple8, judge6, "7.25");
        spreadsheet.put(couple9, judge6, "8.60");
        spreadsheet.put(couple10, judge6, "7.40");
        spreadsheet.put(couple11, judge6, "7.05");
        spreadsheet.put(couple12, judge6, "7.30");

        spreadsheet.put(couple1, judge7, "8.30");
        spreadsheet.put(couple2, judge7, "8.80");
        spreadsheet.put(couple3, judge7, "9.00");
        spreadsheet.put(couple4, judge7, "9.50");
        spreadsheet.put(couple5, judge7, "8.60");
        spreadsheet.put(couple6, judge7, "8.10");
        spreadsheet.put(couple7, judge7, "9.40");
        spreadsheet.put(couple8, judge7, "8.50");
        spreadsheet.put(couple9, judge7, "9.60");
        spreadsheet.put(couple10, judge7, "8.90");
        spreadsheet.put(couple11, judge7, "7.90");
        spreadsheet.put(couple12, judge7, "9.20");

        spreadsheet.put(couple1, judgeChief, "8.90");
        spreadsheet.put(couple2, judgeChief, "9.10");
        spreadsheet.put(couple3, judgeChief, "8.70");
        spreadsheet.put(couple4, judgeChief, "9.40");
        spreadsheet.put(couple5, judgeChief, "9.00");
        spreadsheet.put(couple6, judgeChief, "8.00");
        spreadsheet.put(couple7, judgeChief, "9.30");
        spreadsheet.put(couple8, judgeChief, "8.60");
        spreadsheet.put(couple9, judgeChief, "9.50");
        spreadsheet.put(couple10, judgeChief, "8.80");
        spreadsheet.put(couple11, judgeChief, "8.40");
        spreadsheet.put(couple12, judgeChief, "9.20");

        new RelativeScoringService(spreadsheet).process();
    }

    private static List<RelativeScore> buildScoreObjects(Judge judge1, Map<ScoredDancers, String> scores) {
        return scores.entrySet().stream()
                .map(entry -> buildScoreObject(judge1, entry.getKey(), entry.getValue()))
                .toList();
    }

    private static RelativeScore buildScoreObject(Judge judge, ScoredDancers couple, String rawScore) {
        RelativeScore relativeScore = new RelativeScore();
        relativeScore.setCompetitor(couple);
        relativeScore.setJudge(judge);
        relativeScore.setRawScore(new BigDecimal(rawScore));
        return relativeScore;
    }

    private static ScoredDancers buildScoredDancers(Integer number, List<String> names) {
        ScoredDancers scoredDancers = new ScoredDancers(number, names.stream().map(name -> new Person(UUID.randomUUID().toString(), name)).toList());
        return scoredDancers;
    }
}
