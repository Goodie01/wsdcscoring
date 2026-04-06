package nz.geek.goodwin.scoring.callback;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;
import nz.geek.goodwin.scoring.internal.spreadsheet.HtmlPrinter;

import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class CallBackScoringService {
    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private final Map<String, BigDecimal> scoringMap;

    private Spreadsheet<ScoredDancers, Judge, BigDecimal> ordinalScores;

    public CallBackScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores,
                                  Map<String, BigDecimal> scoringMap) {
        this.rawScores = rawScores;
        this.scoringMap = scoringMap;
    }

    public void process() throws IOException {
        ordinalScores = new Spreadsheet<>();
        ordinalScores.addRows(this.rawScores.getRows());
        ordinalScores.addColumns(
                this.rawScores.getColumns().stream().filter(judge -> !judge.chiefJudge()).toList()
        );

        rawScores.forEach((dancer, judge, score) -> {
            if (judge.chiefJudge()) {
                //TODO what if the chief judge raw scored instead of ordinal scored?
                ordinalScores.put(dancer, judge, new BigDecimal(score));
                return;
            }

            BigDecimal value = scoringMap.get(score);
            if (value != null) {
                ordinalScores.put(dancer, judge, value);
            }
        });

        Path outputFile = Path.of("spreadsheet.html");
        try (PrintStream out = new PrintStream(Files.newOutputStream(outputFile))) {
            HtmlPrinter printer = new HtmlPrinter(out);
            printer.print(ordinalScores, "Ordinal Scores");
        }
    }
}
