package nz.geek.goodwin.scoring.callback;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import nz.geek.goodwin.scoring.internal.spreadsheet.AsciiPrinter;
import nz.geek.goodwin.scoring.internal.spreadsheet.HtmlPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class CallBackScoringService {
    private static final Comparator<Map.Entry<ScoredDancers, BigDecimal>> MAP_COMPARING_BY_VALUE = Map.Entry.<ScoredDancers, BigDecimal>comparingByValue().reversed();

    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private final boolean chiefJudgeUsingRawScores;
    private final int numberGoingThrough;
    private final Map<String, BigDecimal> scoringMap;
    private Spreadsheet<ScoredDancers, Judge, BigDecimal> ordinalScores;
    private Map<ScoredDancers, BigDecimal> finalSum;
    private List<ScoredDancers> finalRanking;
    Spreadsheet<ScoredDancers, String, String> finalOutput;

    public CallBackScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores,
                                  boolean chiefJudgeUsingRawScores,
                                  int numberGoingThrough,
                                  Map<String, BigDecimal> scoringMap) {
        this.rawScores = rawScores;
        this.chiefJudgeUsingRawScores = chiefJudgeUsingRawScores;
        this.numberGoingThrough = numberGoingThrough;
        this.scoringMap = scoringMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        stringBigDecimalEntry -> StringUtils.upperCase(stringBigDecimalEntry.getKey()),
                        Map.Entry::getValue
                ));
    }

    public List<ScoredDancers> getFinalRanking() {
        return finalRanking;
    }

    public void process() throws IOException {
        validateScores();
        calculateOrdinalScores();
        calculateFinalRanking();


        AsciiPrinter asciiPrinter = new AsciiPrinter(System.out);
        asciiPrinter.print(ordinalScores);
        finalRanking.forEach(dancer -> System.out.println(dancer.toString()));

        Path outputFile = Path.of("spreadsheet.html");
        try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile))) {
            HtmlPrinter printer = new HtmlPrinter(out);
            printer.print(ordinalScores, "Ordinal Scores");
        }
    }

    private void calculateFinalRanking() {
        finalSum = new HashMap<>();
        ordinalScores.forEach((dancer, judge, score) -> {
            if (judge.chiefJudge()) {
                return;
            }

            finalSum.compute(dancer, (scoredDancers, bigDecimal) -> {
                if (bigDecimal == null) {
                    return score;
                }
                return bigDecimal.add(score);
            });
        });

        Judge chiefJudge = ordinalScores.getColumns().stream().filter(Judge::chiefJudge).findFirst().orElseThrow();

        List<Map.Entry<ScoredDancers, BigDecimal>> sortedRanking = finalSum.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue().compareTo(o2.getValue()) == 0) {
                        return Comparator.comparing((ScoredDancers o) -> ordinalScores.get(o, chiefJudge))
                                .compare(o1.getKey(), o2.getKey());
                    }
                    return MAP_COMPARING_BY_VALUE.compare(o1, o2);
                })
                .toList();

        this.finalRanking = new ArrayList<>();
        sortedRanking.forEach(entry -> {
            this.finalRanking.add(entry.getKey());
        });
    }

    private void validateScores() {
        this.rawScores.getColumns().forEach(judge -> {
            if (!judge.chiefJudge()) {
                return;
            }
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
        Comparator<Map.Entry<ScoredDancers, String>> lowestFirst =
                Comparator.comparing((Map.Entry<ScoredDancers, String> e) -> new BigDecimal(e.getValue()));
        // Raw scores: highest wins. Ordinals: lowest wins.
        Comparator<Map.Entry<ScoredDancers, String>> comparingFinal = chiefJudgeUsingRawScores ? lowestFirst.reversed() : lowestFirst;

        ordinalScores = new Spreadsheet<>();
        ordinalScores.addRows(this.rawScores.getRows());
        ordinalScores.addColumns(this.rawScores.getColumns());

        this.rawScores.getColumns().forEach(judge -> {
            if (!judge.chiefJudge()) {
                return;
            }

            List<Map.Entry<ScoredDancers, String>> sortedScores = this.rawScores.getAllForColumn(judge)
                    .entrySet()
                    .stream()
                    .sorted(comparingFinal)
                    .toList();

            for (int i = 0; i < sortedScores.size(); i++) {
                ordinalScores.put(sortedScores.get(i).getKey(), judge, BigDecimal.valueOf(i + 1));
            }
        });

        rawScores.forEach((dancer, judge, score) -> {
            if (judge.chiefJudge()) {
                return;
            }

            BigDecimal value = scoringMap.get(StringUtils.upperCase(score));
            if (value != null) {
                ordinalScores.put(dancer, judge, value);
            }
        });
    }
}
