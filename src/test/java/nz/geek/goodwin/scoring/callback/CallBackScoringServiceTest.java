package nz.geek.goodwin.scoring.callback;

import com.github.javafaker.Faker;
import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.Person;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CallBackScoringServiceTest {

    @Test
    public void test() throws IOException {
        Judge judge1 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #1"), false);
        Judge judge2 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #2"), false);
        Judge judge3 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #3"), false);
        Judge judge4 = new Judge(new Person(UUID.randomUUID().toString(), "Judge #4"), false);
        Judge judgeChief = new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true);
        Faker faker = new Faker();
        ScoredDancers couple1 = ScoredDancers.of(1, faker.name().name());
        ScoredDancers couple2 = ScoredDancers.of(2, faker.name().name());
        ScoredDancers couple3 = ScoredDancers.of(3, faker.name().name());
        ScoredDancers couple4 = ScoredDancers.of(4, faker.name().name());
        ScoredDancers couple5 = ScoredDancers.of(5, faker.name().name());
        ScoredDancers couple6 = ScoredDancers.of(6, faker.name().name());
        ScoredDancers couple7 = ScoredDancers.of(7, faker.name().name());
        ScoredDancers couple8 = ScoredDancers.of(8, faker.name().name());
        ScoredDancers couple9 = ScoredDancers.of(9, faker.name().name());
        ScoredDancers couple10 = ScoredDancers.of(10, faker.name().name());
        ScoredDancers couple11 = ScoredDancers.of(11, faker.name().name());
        ScoredDancers couple12 = ScoredDancers.of(12, faker.name().name());
        ScoredDancers couple13 = ScoredDancers.of(13, faker.name().name());
        ScoredDancers couple14 = ScoredDancers.of(14, faker.name().name());
        ScoredDancers couple15 = ScoredDancers.of(15, faker.name().name());
        ScoredDancers couple16 = ScoredDancers.of(16, faker.name().name());
        ScoredDancers couple17 = ScoredDancers.of(17, faker.name().name());
        ScoredDancers couple18 = ScoredDancers.of(18, faker.name().name());
        ScoredDancers couple19 = ScoredDancers.of(19, faker.name().name());
        ScoredDancers couple20 = ScoredDancers.of(20, faker.name().name());
        ScoredDancers couple21 = ScoredDancers.of(21, faker.name().name());
        ScoredDancers couple22 = ScoredDancers.of(22, faker.name().name());
        ScoredDancers couple23 = ScoredDancers.of(23, faker.name().name());
        ScoredDancers couple24 = ScoredDancers.of(24, faker.name().name());
        ScoredDancers couple25 = ScoredDancers.of(25, faker.name().name());
        ScoredDancers couple26 = ScoredDancers.of(26, faker.name().name());
        ScoredDancers couple27 = ScoredDancers.of(27, faker.name().name());
        ScoredDancers couple28 = ScoredDancers.of(28, faker.name().name());
        ScoredDancers couple29 = ScoredDancers.of(29, faker.name().name());
        ScoredDancers couple30 = ScoredDancers.of(30, faker.name().name());

        Spreadsheet<ScoredDancers, Judge, String> inputRawScores = new Spreadsheet<>();
        inputRawScores.addColumns(List.of(judge1, judge2, judge3, judge4, judgeChief));
        inputRawScores.addRow(couple1, "Yes", "yes", "yes", "yes", "1");
        inputRawScores.addRow(couple2, "Yes", "yes", "yes", "yes", "2");
        inputRawScores.addRow(couple3, "Yes", "yes", "yes", "yes", "3");
        inputRawScores.addRow(couple4, "Yes", "yes", "yes", "yes", "4");
        inputRawScores.addRow(couple5, "Yes", "yes", "yes", "no", "5");
        inputRawScores.addRow(couple6, "Yes", "yes", "yes", "no", "6");
        inputRawScores.addRow(couple7, "Yes", "yes", "yes", "no", "7");
        inputRawScores.addRow(couple8, "Yes", "yes", "yes", "no", "8");
        inputRawScores.addRow(couple9, "Yes", "yes", "yes", "no", "9");
        inputRawScores.addRow(couple10, "Yes", "yes", "yes", "no", "10");
        inputRawScores.addRow(couple11, "yes", "yes", "alt_1", "", "11");
        inputRawScores.addRow(couple12, "yes", "yes", "alt_1", "", "12");
        inputRawScores.addRow(couple13, "yes", "yes", "", "", "13");
        inputRawScores.addRow(couple14, "yes", "yes", "", "", "14");
        inputRawScores.addRow(couple15, "yes", "yes", "", "", "15");
        inputRawScores.addRow(couple16, "yes", "yes", "", "", "16");
        inputRawScores.addRow(couple17, "yes", "yes", "", "", "17");
        inputRawScores.addRow(couple18, "yes", "yes", "", "", "18");
        inputRawScores.addRow(couple19, "yes", "yes", "", "", "19");
        inputRawScores.addRow(couple20, "yes", "alt_1", "alt_2", "", "20");
        inputRawScores.addRow(couple21, "yes", "alt_1", "", "", "21");
        inputRawScores.addRow(couple22, "yes", "alt_2", "", "", "22");
        inputRawScores.addRow(couple23, "yes", "alt_2", "", "", "23");
        inputRawScores.addRow(couple24, "yes", "", "", "", "24");
        inputRawScores.addRow(couple25, "yes", "", "", "", "25");
        inputRawScores.addRow(couple26, "yes", "", "", "", "26");
        inputRawScores.addRow(couple27, "yes", "", "", "", "27");
        inputRawScores.addRow(couple28, "alt_2", "", "", "", "28");
        inputRawScores.addRow(couple29, "", "", "", "", "29");
        inputRawScores.addRow(couple30, "", "", "", "", "30");


        new CallBackScoringService(inputRawScores,
                false,
                15,
                Map.of(
                        "Yes", BigDecimal.valueOf(10.0),
                        "alt_1", BigDecimal.valueOf(4.5),
                        "alt_2", BigDecimal.valueOf(4.3),
                        "alt_3", BigDecimal.valueOf(4.2)
                )).process();
    }

    private static final Map<String, BigDecimal> PAPER_VALUES = Map.of(
            "yes", new BigDecimal("10"), "alt1", new BigDecimal("4.5"),
            "alt2", new BigDecimal("4.3"), "alt3", new BigDecimal("4.2"));

    private static Spreadsheet<ScoredDancers, Judge, String> sheet(List<ScoredDancers> couples) {
        Spreadsheet<ScoredDancers, Judge, String> sheet = new Spreadsheet<>();
        sheet.addRows(couples);
        for (int i = 1; i <= 3; i++) {
            sheet.addColumn(new Judge(new Person(UUID.randomUUID().toString(), "Judge #" + i), false));
        }
        sheet.addColumn(new Judge(new Person(UUID.randomUUID().toString(), "Chief Judge"), true));
        return sheet;
    }

    @Test
    public void chiefJudgeTieBreakComparedAsNumbers() throws IOException {
        ScoredDancers a = ScoredDancers.of(1, "A");
        ScoredDancers b = ScoredDancers.of(2, "B");
        Spreadsheet<ScoredDancers, Judge, String> scores = sheet(List.of(a, b));
        scores.addRow(a, "yes", "yes", "no", "10");
        scores.addRow(b, "yes", "yes", "no", "9");

        CallBackScoringService service = new CallBackScoringService(scores, true, 1, PAPER_VALUES);
        service.process();

        assertThat(service.getFinalRanking()).containsExactly(a, b);
    }

    @Test
    public void chiefJudgeOrdinalsLowestWins() throws IOException {
        ScoredDancers a = ScoredDancers.of(1, "A");
        ScoredDancers b = ScoredDancers.of(2, "B");
        Spreadsheet<ScoredDancers, Judge, String> scores = sheet(List.of(a, b));
        scores.addRow(a, "yes", "yes", "no", "2");
        scores.addRow(b, "yes", "yes", "no", "10");

        CallBackScoringService service = new CallBackScoringService(scores, false, 1, PAPER_VALUES);
        service.process();

        assertThat(service.getFinalRanking()).containsExactly(a, b);
    }

    @Test
    public void chiefJudgeNumericallyEqualScoresAreATie() {
        ScoredDancers a = ScoredDancers.of(1, "A");
        ScoredDancers b = ScoredDancers.of(2, "B");
        Spreadsheet<ScoredDancers, Judge, String> scores = sheet(List.of(a, b));
        scores.addRow(a, "yes", "yes", "no", "8.5");
        scores.addRow(b, "yes", "yes", "no", "8.50");

        assertThatThrownBy(() -> new CallBackScoringService(scores, true, 1, PAPER_VALUES).process())
                .hasMessageContaining("did not give out unique scores");
    }

    @Test
    public void equalTotalsWithDifferentDecimalPlacesUseChiefTieBreak() throws IOException {
        ScoredDancers a = ScoredDancers.of(1, "A");
        ScoredDancers b = ScoredDancers.of(2, "B");
        Spreadsheet<ScoredDancers, Judge, String> scores = sheet(List.of(a, b));
        scores.addRow(a, "yes", "no", "no", "1");      // 10
        scores.addRow(b, "alt1", "alt1", "no", "2");   // 5.0 + 5.0 = 10.0, chief prefers B

        CallBackScoringService service = new CallBackScoringService(scores, true, 1,
                Map.of("yes", new BigDecimal("10"), "alt1", new BigDecimal("5.0")));
        service.process();

        assertThat(service.getFinalRanking()).containsExactly(b, a);
    }
}