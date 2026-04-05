package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

public class RelativeScoringService {
    private final PrintStream out = System.out;
    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores;
    private Spreadsheet<ScoredDancers, String, String> majorityTally;
    private Spreadsheet<ScoredDancers, String, Integer> sumOfOrdinalsForTieBreaks;
    private List<ScoredDancers> finalRanking;

    public RelativeScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores) {
        this.rawScores = rawScores;
    }

    public void process() {
        out.println("Initial raw scores:");
        rawScores.output(
                Comparator.comparing(Judge::chiefJudge).thenComparing(Judge::displayName),
                Comparator.comparing(ScoredDancers::bibNumber),
                Judge::displayName,
                ScoredDancers::displayName
        );

        validateScores();

        calculateOrdinalScores();
        out.println("Ordinal scores:");
        ordinalScores.output(
                Comparator.comparing(Judge::chiefJudge).thenComparing(Judge::displayName),
                Comparator.comparing(ScoredDancers::bibNumber),
                Judge::displayName,
                ScoredDancers::displayName
        );

        calculateMajorityTally();
        out.println("Majority tally:");
        majorityTally.output(
                Comparator.comparingInt(RelativeScoringService::majorityOrder),
                Comparator.comparing(ScoredDancers::bibNumber),
                Function.identity(),
                ScoredDancers::displayName
        );

        sumOfOrdinalsForTieBreak();
        out.println("Sum of Ordinals for tie breaks:");
        sumOfOrdinalsForTieBreaks.output(
                Comparator.comparingInt(RelativeScoringService::majorityOrder),
                Comparator.comparing(ScoredDancers::bibNumber),
                Function.identity(),
                ScoredDancers::displayName
        );

        FinalRankingCalculator finalRankingCalculator = new FinalRankingCalculator(majorityTally, ordinalScores);
        finalRankingCalculator.calculateFinalRanking();
        finalRanking = finalRankingCalculator.getFinalRanking();
        finalRanking.forEach(dancer -> out.println("#" + dancer.bibNumber() + ": " + dancer.displayName()));
    }

    private void validateScores() {
        //Rule 3.4 > 7 > b - Odd number of judges used (excluding chief judge)
        this.rawScores.getColumns().forEach(judge -> {
            Map<ScoredDancers, String> allScoresForJudge = rawScores.getAllForColumn(judge);
            allScoresForJudge
                    .forEach((entry, value) -> {
                        if (StringUtils.isBlank(value)) {
                            throw new RuntimeException("Judge " + judge.displayName() + " has not issued a score for competitor " + entry.displayName());
                        }
                    });

            HashSet<String> uniqueScores = new HashSet<>(allScoresForJudge.values());
            if (uniqueScores.size() != allScoresForJudge.size()) {
                throw new RuntimeException("Judge " + judge.displayName() + " did not give out unique scores");
            }
        });
    }

    private void calculateOrdinalScores() {
        Comparator<Map.Entry<ScoredDancers, String>> comparing = Map.Entry.comparingByValue();
        Comparator<Map.Entry<ScoredDancers, String>> comparingFinal = comparing.reversed();

        ordinalScores = new Spreadsheet<>();
        ordinalScores.addRows(this.rawScores.getRows());
        ordinalScores.addColumns(this.rawScores.getColumns());

        ordinalScores.getColumns().forEach(judge -> {
            List<Map.Entry<ScoredDancers, String>> sortedScores = this.rawScores.getAllForColumn(judge)
                    .entrySet()
                    .stream()
                    .sorted(comparingFinal)
                    .toList();

            for (int i = 0; i < sortedScores.size(); i++) {
                ordinalScores.put(sortedScores.get(i).getKey(), judge, i + 1);
            }
        });
    }

    private void calculateMajorityTally() {
        int majorityJudges = (int) Math.ceil(ordinalScores.getColumns().size() / 2.0d);

        majorityTally = new Spreadsheet<>();
        majorityTally.addRows(this.rawScores.getRows());
        Set<ScoredDancers> calculatedDancers = new HashSet<>();

        for (IntHolder holder = new IntHolder(1);
             holder.getValue() < this.rawScores.getRows().size() + 1;
             holder.incValue()) { //For each placing...
            String title = holder.toString();
            majorityTally.addColumn(title);
            ordinalScores.getRows().forEach(dancer -> {
                if (calculatedDancers.contains(dancer)) {
                    majorityTally.put(dancer, title, "-");
                    return;
                }
                long count = filterOrdinalScoresForDancer(dancer, holder.getValue()).count();

                if (count >= majorityJudges) {
                    majorityTally.put(dancer, title, String.valueOf(Math.toIntExact(count)));
                    calculatedDancers.add(dancer);
                }
            });
        }
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

    private void sumOfOrdinalsForTieBreak() {
        sumOfOrdinalsForTieBreaks = new Spreadsheet<>();
        sumOfOrdinalsForTieBreaks.addRows(rawScores.getRows());
        sumOfOrdinalsForTieBreaks.addColumns(majorityTally.getColumns());

        for (IntHolder holder = new IntHolder(1);
             holder.getValue() < this.rawScores.getRows().size() + 1;
             holder.incValue()) { //For each placing...

            Map<ScoredDancers, String> allForColumn = majorityTally.getAllForColumn(holder.toString());

            allForColumn.forEach((scoredDancers, value) -> {
                if (Strings.CI.equals("-", value)) {
                    return;
                }
                long count = allForColumn.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue().equals(value))
                        .count();

                if (count > 1) {
                    int sum = filterOrdinalScoresForDancer(scoredDancers, holder.getValue()).sum();
                    sumOfOrdinalsForTieBreaks.put(scoredDancers, holder.toString(), sum);
                }
            });
        }
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
}
