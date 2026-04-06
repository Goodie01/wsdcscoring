package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.Person;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RelativeScoringServiceTest {
    @Test
    public void test() throws IOException {
        Judge judge1 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #1"), false);
        Judge judge2 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #2"), false);
        Judge judge3 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #3"), false);
        Judge judge4 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #4"), false);
        Judge judge5 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #5"), false);
        Judge judge6 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #6"), false);
        Judge judge7 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #7"), false);
        Judge judgeChief = new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true);

        ScoredDancers couple1 = ScoredDancers.of(1, "Romie", "Julie");
        ScoredDancers couple2 = ScoredDancers.of(2, "Marc", "Cleo");
        ScoredDancers couple3 = ScoredDancers.of(3, "George", "Gracie");
        ScoredDancers couple4 = ScoredDancers.of(4, "Jack", "Annie");
        ScoredDancers couple5 = ScoredDancers.of(5, "Rhett", "Scarlet");
        ScoredDancers couple6 = ScoredDancers.of(6, "Rocky", "Adrian");
        ScoredDancers couple7 = ScoredDancers.of(7, "Fred", "Ginger");
        ScoredDancers couple8 = ScoredDancers.of(8, "Barney", "Betty");
        ScoredDancers couple9 = ScoredDancers.of(9, "Ricky", "Lucy");
        ScoredDancers couple10 = ScoredDancers.of(10, "Ken", "Barbie");
        ScoredDancers couple11 = ScoredDancers.of(11, "Ike", "Mamie");
        ScoredDancers couple12 = ScoredDancers.of(12, "Ward", "June");

        Spreadsheet<ScoredDancers, Judge, String> inputRawScores = new Spreadsheet<>();
        inputRawScores.addRows(List.of(couple1, couple2, couple3, couple4, couple5, couple6, couple7, couple8, couple9, couple10, couple11, couple12));
        inputRawScores.addColumn(judge1, "8.75", "8.63", "8.85", "9.00", "8.69", "8.60", "9.10", "8.64", "8.90", "8.65", "8.40", "8.67");
        inputRawScores.addColumn(judge2, "8.00", "8.60", "8.25", "8.38", "8.30", "8.10", "8.35", "8.20", "8.50", "8.32", "8.23", "8.15");
        inputRawScores.addColumn(judge3, "9.34", "9.20", "9.33", "9.35", "9.31", "9.65", "9.70", "9.25", "9.72", "9.32", "9.28", "9.45");
        inputRawScores.addColumn(judge4, "7.70", "8.60", "7.90", "9.00", "8.50", "7.80", "8.80", "7.95", "8.20", "8.25", "7.75", "8.30");
        inputRawScores.addColumn(judge5, "9.10", "9.15", "9.20", "9.60", "9.50", "9.30", "8.70", "9.25", "9.40", "8.60", "8.65", "9.12");
        inputRawScores.addColumn(judge6, "7.00", "7.20", "7.60", "8.50", "7.24", "7.10", "7.15", "7.25", "8.60", "7.40", "7.05", "7.30");
        inputRawScores.addColumn(judge7, "8.30", "8.80", "9.00", "9.50", "8.60", "8.10", "9.40", "8.50", "9.60", "8.90", "7.90", "9.20");
        inputRawScores.addColumn(judgeChief, "8.90", "9.10", "8.70", "9.40", "9.00", "8.00", "9.30", "8.60", "9.50", "8.80", "8.40", "9.20");

        new RelativeScoringService(inputRawScores).process();
    }

}