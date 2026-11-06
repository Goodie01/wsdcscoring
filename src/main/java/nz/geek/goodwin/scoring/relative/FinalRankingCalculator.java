package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.apache.commons.lang3.Strings;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.IntStream;

public class FinalRankingCalculator {
    private static final Comparator<Map.Entry<ScoredDancers, String>> MAP_COMPARING_BY_VALUE =
            Comparator.comparing((Map.Entry<ScoredDancers, String> e) -> Integer.parseInt(e.getValue())).reversed();

    private final PrintStream out = System.out;
    private final Spreadsheet<ScoredDancers, String, String> majorityTally;
    private final Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores;
    private List<ScoredDancers> finalRanking;
    private Spreadsheet<ScoredDancers, String, String> finalOutput;

    public FinalRankingCalculator(Spreadsheet<ScoredDancers, String, String> majorityTally,
                                  Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores) {
        this.majorityTally = majorityTally;
        this.ordinalScores = ordinalScores;
    }

    public List<ScoredDancers> getFinalRanking() {
        return finalRanking;
    }

    public Spreadsheet<ScoredDancers, String, String> getFinalOutput() {
        return finalOutput;
    }

    public void calculateFinalRanking() {
        this.finalRanking = new ArrayList<>();
        this.finalOutput = new Spreadsheet<>();
        this.finalOutput.addRows(this.majorityTally.getRows());
        this.finalOutput.addColumn("Final Ranking");

        ordinalScores.getColumns().forEach(judge -> {
            if (judge.chiefJudge()) {
                return;
            }
            this.finalOutput.addColumn(judge.toString());
        });

        ordinalScores.forEach((dancer, judge, score) -> {
            if (judge.chiefJudge()) {
                return;
            }

            this.finalOutput.put(dancer, judge.toString(), String.valueOf(score));
        });

        List<String> columns = new ArrayList<>(this.majorityTally.getColumns()).stream()
                .sorted(Comparator.comparing(FinalRankingCalculator::majorityOrder))
                .toList();
        for (int i = 0; i < columns.size(); i++) {
            String columnTitle = columns.get(i);
            int level = majorityOrder(columnTitle); //Turns something like 'Maj: 1st to 6th' to 6

            this.finalOutput.addColumn(columnTitle);
            this.majorityTally.getAllForColumn(columnTitle).forEach((entry, value) -> {
                this.finalOutput.put(entry, columnTitle, value);
            });

            List<Map.Entry<ScoredDancers, String>> list = this.majorityTally.getAllForColumn(columnTitle)
                    .entrySet()
                    .stream()
                    .filter(entry -> !Strings.CI.equals("-", entry.getValue()))
                    .sorted((o1, o2) -> {
                        if (o1.getValue().equals(o2.getValue())) {
                            return tieBreakCompareSum(o1.getKey(), o2.getKey(), level, level);
                        }
                        return MAP_COMPARING_BY_VALUE.compare(o1, o2);
                    })
                    .toList();

            list.forEach(entry -> {
                this.finalRanking.add(entry.getKey());
            });
        }

        for (int i = 0; i < this.finalRanking.size(); i++) {
            this.finalOutput.put(this.finalRanking.get(i), "Final Ranking", String.valueOf(i + 1));
        }

        this.finalOutput.getRows().sort(Comparator.comparing(o -> this.finalRanking.indexOf(o)));
    }

    private int tieBreakCompareSum(ScoredDancers o1, ScoredDancers o2, int originalLevel, int level) {
        if (level > ordinalScores.getRows().size()) {
            throw new RuntimeException("Too many levels");
        }

        int o1Sum = filterOrdinalScoresForDancer(o1, level).sum();
        int o2Sum = filterOrdinalScoresForDancer(o2, level).sum();

        String newString = new IntHolder(originalLevel).toString() + " sum tie break at " + level;
        this.finalOutput.addColumn(newString);
        this.finalOutput.put(o1, newString, String.valueOf(o1Sum));
        this.finalOutput.put(o2, newString, String.valueOf(o2Sum));

        if (o1Sum == o2Sum) {
            return tieBreakCompareCount(o1, o2, originalLevel, level + 1);
        }

        return Integer.compare(o1Sum, o2Sum);
    }

    private int tieBreakCompareCount(ScoredDancers o1, ScoredDancers o2, int originalLevel, int level) {
        if (level > ordinalScores.getRows().size()) {
            return tieBreakCompareFinal(o1, o2, originalLevel, level);
        }

        long o1Count = filterOrdinalScoresForDancer(o1, level).count();
        long o2Count = filterOrdinalScoresForDancer(o2, level).count();


        String newString = new IntHolder(originalLevel).toString() + " count tie break at " + level;
        this.finalOutput.addColumn(newString);
        this.finalOutput.put(o1, newString, String.valueOf(o1Count));
        this.finalOutput.put(o2, newString, String.valueOf(o2Count));

        if (o1Count == o2Count) {
            return tieBreakCompareCount(o1, o2, originalLevel, level + 1);
        }

        return Long.compare(o2Count, o1Count);
    }

    private int tieBreakCompareFinal(ScoredDancers o1, ScoredDancers o2, int originalLevel, int level) {
        System.out.println("Final tie break begin");

        int o1Sum = 0;
        int o2Sum = 0;

        for (Judge judge : ordinalScores.getColumns()) {
            if (judge.chiefJudge()) {
                continue;
            }
            Integer dancer1Score = ordinalScores.get(o1, judge);
            Integer dancer2Score = ordinalScores.get(o2, judge);

            if (dancer1Score > dancer2Score) {
                o1Sum++;
            } else if (dancer1Score < dancer2Score) {
                o2Sum++;
            }
        }

        String newString = new IntHolder(originalLevel).toString() + " final break at " + level;
        this.finalOutput.addColumn(newString);
        this.finalOutput.put(o1, newString, String.valueOf(o1Sum));
        this.finalOutput.put(o2, newString, String.valueOf(o2Sum));

        return Integer.compare(o1Sum, o2Sum);
    }

    private static int majorityOrder(String value) {
        if (value == null) {
            return Integer.MAX_VALUE;
        }

        if (value.equals("Maj: 1st")) {
            return 1;
        }

        String suffix = value.substring("Maj: 1st to ".length());

        int end = suffix.length() - 2; // removes st/nd/rd/th
        return Integer.parseInt(suffix.substring(0, end));
    }

    private IntStream filterOrdinalScoresForDancer(ScoredDancers dancer, Integer scoresUpFrom) {
        return ordinalScores.getAllForRow(dancer)
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().chiefJudge())
                .filter(entry -> entry.getValue() != null)
                .filter(entry -> entry.getValue() <= scoresUpFrom)
                .mapToInt(Map.Entry::getValue);
    }
}
