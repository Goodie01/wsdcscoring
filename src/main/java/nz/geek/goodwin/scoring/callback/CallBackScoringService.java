package nz.geek.goodwin.scoring.callback;

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

public class CallBackScoringService {
    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private final Map<String, BigDecimal> scoringMap;

    private Spreadsheet<ScoredDancers, Judge, BigDecimal> ordinalScores;

    public CallBackScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores,
                                  Map<String, BigDecimal> scoringMap) {
        this.rawScores = rawScores;
        this.scoringMap = scoringMap.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        stringBigDecimalEntry -> StringUtils.upperCase(stringBigDecimalEntry.getKey()),
                        Map.Entry::getValue
                ));
    }

    public void process() throws IOException {
        validateScores();
        calculateOrbinalScores();

        Path outputFile = Path.of("spreadsheet.html");
        try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile))) {
            HtmlPrinter printer = new HtmlPrinter(out);
            printer.print(ordinalScores, "Ordinal Scores");
        }
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

            HashSet<String> uniqueScores = new HashSet<>(allScoresForJudge.values());
            if (uniqueScores.size() != allScoresForJudge.size()) {
                throw new RuntimeException("Judge " + judge + " did not give out unique scores");
            }
        });
    }

    private void calculateOrbinalScores() {
        Comparator<Map.Entry<ScoredDancers, String>> comparing = Map.Entry.comparingByValue();
        Comparator<Map.Entry<ScoredDancers, String>> comparingFinal = comparing.reversed();

        ordinalScores = new Spreadsheet<>();
        ordinalScores.addRows(this.rawScores.getRows());
        ordinalScores.addColumns(this.rawScores.getColumns());

        rawScores.forEach((dancer, judge, score) -> {
            if (judge.chiefJudge()) {
                List<Map.Entry<ScoredDancers, String>> sortedScores = this.rawScores.getAllForColumn(judge)
                        .entrySet()
                        .stream()
                        .sorted(comparingFinal)
                        .toList();

                for (int i = 0; i < sortedScores.size(); i++) {
                    ordinalScores.put(sortedScores.get(i).getKey(), judge, BigDecimal.valueOf(i + 1));
                }

                return;
            }

            BigDecimal value = scoringMap.get(StringUtils.upperCase(score));
            if (value != null) {
                ordinalScores.put(dancer, judge, value);
            }
        });
    }
}
