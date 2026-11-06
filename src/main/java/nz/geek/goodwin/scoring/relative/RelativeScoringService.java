package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import nz.geek.goodwin.scoring.internal.spreadsheet.HtmlPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RelativeScoringService {
    private final boolean judgesUsingRawScores;

    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores;
    private Spreadsheet<ScoredDancers, String, String> majorityTally;
    private List<ScoredDancers> finalRanking;

    public RelativeScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores, boolean judgesUsingRawScores) {
        this.rawScores = rawScores;
        this.judgesUsingRawScores = judgesUsingRawScores;
    }

    public void process() throws IOException {
        validateScores();
        calculateOrdinalScores();
        calculateMajorityTally();

        FinalRankingCalculator finalRankingCalculator = new FinalRankingCalculator(majorityTally, ordinalScores);
        finalRankingCalculator.calculateFinalRanking();
        finalRanking = finalRankingCalculator.getFinalRanking();
        Spreadsheet<ScoredDancers, String, String> finalOutput = finalRankingCalculator.getFinalOutput();

        Path outputFile = Path.of("spreadsheet.html");
        try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile))) {
            HtmlPrinter printer = new HtmlPrinter(out);
            printer.print(finalOutput, "Ordinal Scores");
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

            Set<BigDecimal> uniqueScores = allScoresForJudge.values().stream()
                    .map(v -> new BigDecimal(v).stripTrailingZeros())
                    .collect(Collectors.toSet());
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

        Comparator<Map.Entry<ScoredDancers, String>> comparingFinal =
                Comparator.comparing((Map.Entry<ScoredDancers, String> e) -> new BigDecimal(e.getValue())).reversed();

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

        int sizeOfRawScores = this.rawScores.getRows().size();
        for (IntHolder holder = new IntHolder(1); holder.getValue() <= sizeOfRawScores; holder.incValue()) { //For each placing...
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

    public List<ScoredDancers> getFinalRanking() {
        return finalRanking;
    }
}
