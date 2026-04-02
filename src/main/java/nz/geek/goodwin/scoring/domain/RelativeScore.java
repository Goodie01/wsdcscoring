package nz.geek.goodwin.scoring.domain;

import java.math.BigDecimal;

public class RelativeScore {
    private ScoredDancers competitor;
    private Judge judge;
    private BigDecimal rawScore;
    private Integer ordinalScore;

    public ScoredDancers getCompetitor() {
        return competitor;
    }

    public void setCompetitor(ScoredDancers competitor) {
        this.competitor = competitor;
    }

    public Judge getJudge() {
        return judge;
    }

    public void setJudge(Judge judge) {
        this.judge = judge;
    }

    public BigDecimal getRawScore() {
        return rawScore;
    }

    public void setRawScore(BigDecimal rawScore) {
        this.rawScore = rawScore;
    }

    public Integer getOrdinalScore() {
        return ordinalScore;
    }

    public void setOrdinalScore(Integer ordinalScore) {
        this.ordinalScore = ordinalScore;
    }
}
