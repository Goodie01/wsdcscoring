package nz.geek.goodwin.scoring.relative;

import nz.geek.goodwin.scoring.domain.Judge;
import nz.geek.goodwin.scoring.domain.ScoredDancers;
import nz.geek.goodwin.scoring.domain.internal.Spreadsheet;
import org.apache.commons.lang3.Strings;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class FinalRankingCalculator {
    private final PrintStream out = System.out;
    private final Spreadsheet<ScoredDancers, String, String> majorityTally;
    private final Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores;
    private List<ScoredDancers> finalRanking;

    public FinalRankingCalculator(Spreadsheet<ScoredDancers, String, String> majorityTally,
                                  Spreadsheet<ScoredDancers, Judge, Integer> ordinalScores) {
        this.majorityTally = majorityTally;
        this.ordinalScores = ordinalScores;
    }

    public List<ScoredDancers> getFinalRanking() {
        return finalRanking;
    }

    public void calculateFinalRanking() {
        this.finalRanking = new ArrayList<>();

        for (int i = 0; i < this.majorityTally.getRows().size(); i++) {
            Spreadsheet<ScoredDancers, String, String> remainingDancers = new Spreadsheet<>(majorityTally);
            remainingDancers.removeRows(finalRanking);

            ScoredDancers highestRanked = findHighestRanked(remainingDancers);

            if (highestRanked != null) {
                finalRanking.add(highestRanked);
            }
        }
    }

    //Remaining dancers is from the majority tally
    private ScoredDancers findHighestRanked(Spreadsheet<ScoredDancers, String, String> remainingDancers) {
        for (IntHolder searchSpace = new IntHolder(1);
             searchSpace.getValue() <= remainingDancers.getRows().size();
             searchSpace.incValue()) { //For each placing...
            //A single column from the majority tally
            Map<ScoredDancers, String> allForColumn = remainingDancers.getAllForColumn(searchSpace.toString());

            for (Map.Entry<ScoredDancers, String> e : allForColumn.entrySet()) {
                ScoredDancers scoredDancer = e.getKey();
                String value = e.getValue();
                if (Strings.CI.equals("-", value)) {
                    continue;
                }
                int myValue = Integer.parseInt(value);

                long numberOfDancersWithMoreScoresThanMe = findMajorityDancers(allForColumn, entry -> {
                    int theirValue = Integer.parseInt(entry.getValue());
                    return myValue < theirValue;
                }).size();

                if (numberOfDancersWithMoreScoresThanMe != 0) {
                    continue; //There is someone with a higher score, move on to the next dancer
                }

                List<ScoredDancers> dancersnWithScoresEqualToMe = findMajorityDancers(allForColumn, entry -> {
                    int theirValue = Integer.parseInt(entry.getValue());
                    return myValue == theirValue;
                });

                int numberOfDancersWithScoresEqualToMe = dancersnWithScoresEqualToMe.size();
                if (numberOfDancersWithScoresEqualToMe == 1) {
                    return scoredDancer;
                } else if (numberOfDancersWithScoresEqualToMe > 1) {
                    ScoredDancers highestRankedBySumOfOrdinals = findHighestRankedBySumOfOrdinals(scoredDancer, allForColumn, searchSpace.getValue());

                    if (highestRankedBySumOfOrdinals == null) {
                        continue;
                    }

                    return highestRankedBySumOfOrdinals;
                }
            }
        }
        return null;
    }

    private ScoredDancers findHighestRankedBySumOfOrdinals(ScoredDancers currentDancer, Map<ScoredDancers, String> allForColumn, int scoresUpFrom) {
        if (scoresUpFrom > ordinalScores.getColumns().size()) {
            return null;
        }

        int myValue = filterOrdinalScoresForDancer(currentDancer, entry -> entry.getValue() <= scoresUpFrom).sum();

        long numberOfDancersWithLowerSumOfORdinalsThanMe = allForColumn.keySet().stream()
                .mapToInt(scoredDancers1 -> {
                    return filterOrdinalScoresForDancer(scoredDancers1, entry -> entry.getValue() <= scoresUpFrom).sum();
                })
                .filter(sum -> sum < myValue)
                .count();

        System.out.println(scoresUpFrom + "Attempting tie break: " + currentDancer.displayName());
        if (numberOfDancersWithLowerSumOfORdinalsThanMe != 0) {
            return null;
        }

        long numberOfDancersWithOrdinalsEqualToMe = allForColumn.keySet().stream()
                .map(scoredDancers1 ->
                        filterOrdinalScoresForDancer(scoredDancers1, entry -> entry.getValue() >= scoresUpFrom).sum())
                .filter(sum -> sum > myValue)
                .count();

        if (numberOfDancersWithOrdinalsEqualToMe == 1) {
            return currentDancer;
        } else if (numberOfDancersWithOrdinalsEqualToMe > 1) {
            return findHighestRankedBySumOfOrdinals(currentDancer, allForColumn, scoresUpFrom + 1);
        }

        return null;
    }

    private List<ScoredDancers> findMajorityDancers(Map<ScoredDancers, String> allForColumn, Predicate<Map.Entry<ScoredDancers, String>> predicate) {
        return allForColumn.entrySet()
                .stream()
                .filter(entry -> !finalRanking.contains(entry.getKey()))
                .filter(entry -> !Strings.CI.equals("-", entry.getValue()))
                .filter(predicate)
                .map(Map.Entry::getKey)
                .toList();
    }


    private IntStream filterOrdinalScoresForDancer(ScoredDancers dancer, Predicate<Map.Entry<Judge, Integer>> predicate) {
        return ordinalScores.getAllForRow(dancer)
                .entrySet()
                .stream()
                .filter(entry -> !entry.getKey().chiefJudge())
                .filter(entry -> entry.getValue() != null)
                .filter(predicate)
                .mapToInt(Map.Entry::getValue);
    }

}
