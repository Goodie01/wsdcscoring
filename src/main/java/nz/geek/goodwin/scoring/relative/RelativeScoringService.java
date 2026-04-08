package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import nz.geek.goodwin.scoring.internal.spreadsheet.AsciiPrinter;
import nz.geek.goodwin.scoring.internal.spreadsheet.HtmlPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class RelativeScoringService {
    private final PrintStream out = System.out;
    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private final boolean judgesUsingRawScores;
    private Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores;
    private Spreadsheet<ScoredDancers, String, String> majorityTally;
    private Spreadsheet<ScoredDancers, String, Integer> sumOfOrdinalsForTieBreaks;
    private List<ScoredDancers> finalRanking;

    public RelativeScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores, boolean judgesUsingRawScores) {
        this.rawScores = rawScores;
        this.judgesUsingRawScores = judgesUsingRawScores;
    }

    public void process() throws IOException {
        AsciiPrinter asciiPrinter = new AsciiPrinter(out);
        out.println("Initial raw scores:");
        asciiPrinter.print(rawScores);

        validateScores();

        calculateOrdinalScores();
        out.println("Ordinal scores:");
        asciiPrinter.print(ordinalScores);

        calculateMajorityTally();
        out.println("Majority tally:");
        asciiPrinter.print(majorityTally);

        sumOfOrdinalsForTieBreak();
        out.println("Sum of Ordinals for tie breaks:");
        asciiPrinter.print(sumOfOrdinalsForTieBreaks);

        FinalRankingCalculator finalRankingCalculator = new FinalRankingCalculator(majorityTally, ordinalScores);
        finalRankingCalculator.calculateFinalRanking();
        finalRanking = finalRankingCalculator.getFinalRanking();
        for (int i = 0; i < finalRanking.size(); i++) {
            ScoredDancers dancer = finalRanking.get(i);
            out.println((i+1) + ": " + dancer.toString());
        }

        Path outputFile = Path.of("spreadsheet.html");
        try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile))) {
            HtmlPrinter printer = new HtmlPrinter(out);
            printer.print(ordinalScores, "Ordinal Scores");
        }
    }

    private void validateScores() {
        //Rule 3.4 > 7 > b - Odd number of judges used (excluding chief judge)
        if (this.rawScores.getColumns().stream().filter(judge -> !judge.chiefJudge()).count() % 2 == 0) {
            throw new RuntimeException("Must use an odd number of judges");
        }

        if (this.rawScores.getColumns().stream().filter(Judge::chiefJudge).count() != 1) {
            throw new RuntimeException("There must be only one chief judge");
        }

        this.rawScores.getColumns().forEach(judge -> {
            Map<ScoredDancers, String> allScoresForJudge = rawScores.getAllForColumn(judge);
            allScoresForJudge
                    .forEach((entry, value) -> {
                        if (StringUtils.isBlank(value)) {
                            throw new RuntimeException("Judge " + judge + " has not issued a score for competitor " + entry);
                        }
                    });

            HashSet<String> uniqueScores = new HashSet<>(allScoresForJudge.values());
            if (uniqueScores.size() != allScoresForJudge.size()) {
                throw new RuntimeException("Judge " + judge + " did not give out unique scores");
            }
        });
    }

    private void calculateOrdinalScores() {
        if (!judgesUsingRawScores) {
            ordinalScores = new Spreadsheet<>();
            ordinalScores.addRows(this.rawScores.getRows());
            ordinalScores.addColumns(this.rawScores.getColumns());
            rawScores.forEach((dancer, judge, score) -> ordinalScores.put(dancer, judge, Integer.parseInt(score)));
            return;
        }

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

    public List<ScoredDancers> getFinalRanking() {
        return finalRanking;
    }
}
