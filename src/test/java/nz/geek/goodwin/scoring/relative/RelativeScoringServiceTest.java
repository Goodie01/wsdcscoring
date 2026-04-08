package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.Person;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.assertj.core.api.Assertions;
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

        RelativeScoringService relativeScoringService = new RelativeScoringService(inputRawScores, true);
        relativeScoringService.process();

        Assertions.assertThat(relativeScoringService.getFinalRanking())
                .containsExactly(couple4, couple9, couple7, couple12, couple3, couple5, couple10, couple2, couple8, couple6, couple1, couple11);
    }
    @Test
    public void lastTieBreakTest() throws IOException {
        Judge judge1 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #1"), false);
        Judge judge2 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #2"), false);
        Judge judge3 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #3"), false);
        Judge judge4 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #4"), false);
        Judge judge5 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #5"), false);
        Judge judge6 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #6"), false);
        Judge judge7 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #7"), false);
        Judge judgeChief = new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true);

        ScoredDancers couple1 = ScoredDancers.of(1, "Couple A");
        ScoredDancers couple2 = ScoredDancers.of(2, "Couple B");
        ScoredDancers couple3 = ScoredDancers.of(2, "Couple C, other");

        Spreadsheet<ScoredDancers, Judge, String> inputRawScores = new Spreadsheet<>();
        inputRawScores.addRows(List.of(couple1, couple2, couple3));
        inputRawScores.addColumn(judge1, "1", "2", "3");
        inputRawScores.addColumn(judge2, "2", "1", "3");
        inputRawScores.addColumn(judge3, "1", "3", "2");
        inputRawScores.addColumn(judge4, "2", "1", "3");
        inputRawScores.addColumn(judge5, "3", "2", "1");
        inputRawScores.addColumn(judge6, "2", "1", "3");
        inputRawScores.addColumn(judge7, "1", "2", "3");
        inputRawScores.addColumn(judgeChief, "1", "2", "3");

        RelativeScoringService relativeScoringService = new RelativeScoringService(inputRawScores, false);
        relativeScoringService.process();

        Assertions.assertThat(relativeScoringService.getFinalRanking())
                .containsExactly(couple2, couple1, couple3);
    }

    @Test
    public void swingvasionTest() throws IOException {
        Judge judge1 = new Judge(new Person(UUID.randomUUID().toString(), "EG"), false);
        Judge judge2 = new Judge(new Person(UUID.randomUUID().toString(), "MPV"), false);
        Judge judge3 = new Judge(new Person(UUID.randomUUID().toString(), "KR"), false);
        Judge judge4 = new Judge(new Person(UUID.randomUUID().toString(), "TSW"), false);
        Judge judge5 = new Judge(new Person(UUID.randomUUID().toString(), "CF"), false);
        Judge judgeChief = new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true);

        ScoredDancers couple1 = ScoredDancers.of(214, "Tom Gillespie", "Alexa Patterson");
        ScoredDancers couple2 = ScoredDancers.of(194, "Corey Jenkins", "Cara Horisk");
        ScoredDancers couple3 = ScoredDancers.of(192, "Shweta Mehta", "Ren Pritchard");
        ScoredDancers couple4 = ScoredDancers.of(195, "Tim Heere", "Lucy Bekker");
        ScoredDancers couple5 = ScoredDancers.of(101, "Charlotte Holmes", "Lily Partington");
        ScoredDancers couple6 = ScoredDancers.of(176, "Jaimee Thomas", "Ingeborg Gruner Skram ");
        ScoredDancers couple7 = ScoredDancers.of(113, "Graham Aiken", "Georgia Coburn");
        ScoredDancers couple8 = ScoredDancers.of(155, "Mark Akula-Gray", "Holly Gillett");
        ScoredDancers couple9 = ScoredDancers.of(125, "Todd Marles", "Harshi Sisodia");
        ScoredDancers couple10 = ScoredDancers.of(119, "Chris Ward", "Solana Carpenter");
        ScoredDancers couple11 = ScoredDancers.of(135, "Levi Van Rheenen", "Logan Clarricoats");
        ScoredDancers couple12 = ScoredDancers.of(199, "Blake Bedford-Palmer", "Hanna Lu");
        ScoredDancers couple13 = ScoredDancers.of(233, "Bor-Kuan (BK) Song", "Claire O'Keeffe");
        ScoredDancers couple14 = ScoredDancers.of(232, "Chris Fusco", "Carolyn Stanfield");
        ScoredDancers couple15 = ScoredDancers.of(215, "Melissa Gillespie", "Sheena Sadhu");

        Spreadsheet<ScoredDancers, Judge, String> inputRawScores = new Spreadsheet<>();
        inputRawScores.addRows(List.of(couple1, couple2, couple3, couple4, couple5, couple6, couple7, couple8, couple9, couple10, couple11, couple12, couple13, couple14, couple15));
        inputRawScores.addColumn(judge1, "1", "7", "2", "8", "4", "6", "3", "14", "9", "15", "10", "5", "12", "11", "13");
        inputRawScores.addColumn(judge2, "9", "2", "1", "3", "11", "6", "8", "4", "7", "5", "10", "15", "12", "13", "14");
        inputRawScores.addColumn(judge3, "1", "7", "12", "5", "9", "11", "10", "6", "3", "13", "2", "4", "8", "15", "14");
        inputRawScores.addColumn(judge4, "3", "1", "10", "12", "6", "7", "4", "8", "11", "5", "2", "13", "9", "14", "15");
        inputRawScores.addColumn(judge5, "2", "1", "4", "3", "6", "5",  "7", "8", "12", "9", "10", "11", "15", "13", "14");
        inputRawScores.addColumn(judgeChief, "2", "1", "4", "3", "6", "5",  "7", "8", "12", "9", "10", "11", "15", "13", "14");

        RelativeScoringService relativeScoringService = new RelativeScoringService(inputRawScores, false);
        relativeScoringService.process();

        Assertions.assertThat(relativeScoringService.getFinalRanking())
                .containsExactly(couple1, couple2, couple3, couple4, couple5, couple6, couple7, couple8, couple9, couple10, couple11, couple12, couple13, couple14, couple15);
    }

}