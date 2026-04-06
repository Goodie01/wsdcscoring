package nz.geek.goodwin.scoring.callback;

import nz.geek.goodwin.scoring.domain.Spreadsheet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CallBackScoringServiceTest {

    @Test
    public void test() throws IOException {
        new CallBackScoringService(new Spreadsheet<>(), Map.of(
                "Yes", BigDecimal.valueOf(10.0),
                "alt_1", BigDecimal.valueOf(4.5),
                "alt_2", BigDecimal.valueOf(4.3),
                "alt_3", BigDecimal.valueOf(4,2)
        )).process();
    }
}