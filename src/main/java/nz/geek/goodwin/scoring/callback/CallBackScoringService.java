package nz.geek.goodwin.scoring.callback;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.Spreadsheet;

import java.math.BigDecimal;
import java.util.Map;

public class CallBackScoringService {
    private final Spreadsheet<ScoredDancers, Judge, String> rawScores;
    private final Map<String, BigDecimal> scoringMap;

    public CallBackScoringService(Spreadsheet<ScoredDancers, Judge, String> rawScores,
                                  Map<String, BigDecimal> scoringMap) {
        this.rawScores = rawScores;
        this.scoringMap = scoringMap;
    }
}
