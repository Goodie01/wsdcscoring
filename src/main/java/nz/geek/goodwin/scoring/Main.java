package nz.geek.goodwin.scoring;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.Person;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import nz.geek.goodwin.scoring.relative.RelativeScoringService;

import java.io.IOException;
import java.util.*;

public class Main {
    static void main() throws IOException {
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
//        spreadsheet.addColumns(List.of(judge1, judge2, judge3, judge4, judge5, judge6, judge7, judgeChief));
        spreadsheet.addRows(List.of(couple1, couple2, couple3, couple4, couple5, couple6, couple7, couple8, couple9, couple10, couple11, couple12));

        spreadsheet.addColumn(judge1, "8.75", "8.63", "8.85", "9.00", "8.69", "8.60", "9.10", "8.64", "8.90", "8.65", "8.40", "8.67");
        spreadsheet.addColumn(judge2, "8.00", "8.60", "8.25", "8.38", "8.30", "8.10", "8.35", "8.20", "8.50", "8.32", "8.23", "8.15");
        spreadsheet.addColumn(judge3, "9.34", "9.20", "9.33", "9.35", "9.31", "9.65", "9.70", "9.25", "9.72", "9.32", "9.28", "9.45");
        spreadsheet.addColumn(judge4, "7.70", "8.60", "7.90", "9.00", "8.50", "7.80", "8.80", "7.95", "8.20", "8.25", "7.75", "8.30");
        spreadsheet.addColumn(judge5, "9.10", "9.15", "9.20", "9.60", "9.50", "9.30", "8.70", "9.25", "9.40", "8.60", "8.65", "9.12");
        spreadsheet.addColumn(judge6, "7.00", "7.20", "7.60", "8.50", "7.24", "7.10", "7.15", "7.25", "8.60", "7.40", "7.05", "7.30");
        spreadsheet.addColumn(judge7, "8.30", "8.80", "9.00", "9.50", "8.60", "8.10", "9.40", "8.50", "9.60", "8.90", "7.90", "9.20");
        spreadsheet.addColumn(judgeChief, "8.90", "9.10", "8.70", "9.40", "9.00", "8.00", "9.30", "8.60", "9.50", "8.80", "8.40", "9.20");

        new RelativeScoringService(spreadsheet).process();
    }

    private static ScoredDancers buildScoredDancers(Integer number, List<String> names) {
        ScoredDancers scoredDancers = new ScoredDancers(number, names.stream().map(name -> new Person(UUID.randomUUID().toString(), name)).toList());
        return scoredDancers;
    }
}
