package nz.geek.goodwin.scoring.internal.spreadsheet;

import nz.geek.goodwin.scoring.domain.Spreadsheet;

import java.io.PrintStream;
import java.io.PrintWriter;

public class AsciiPrinter {
    private final PrintStream out;

    public AsciiPrinter(PrintStream out) {
        this.out = out;
    }

    public <Row, Column> void print(Spreadsheet<Row, Column, ?> spreadsheet) {

        int rowWidth = Math.max(8, spreadsheet.getRows().stream()
                .mapToInt(d -> d.toString().length())
                .max()
                .orElse(8));

        int cellWidth = Math.max(8, spreadsheet.getColumns().stream()
                .mapToInt(j -> j.toString().length())
                .max()
                .orElse(8));

        out.printf("%-" + rowWidth + "s", "Dancer");

        for (Column column : spreadsheet.getColumns()) {
            out.printf(" | %-" + cellWidth + "s", column.toString());
        }
        out.println();

        out.println("-".repeat(rowWidth + (cellWidth + 3) * spreadsheet.getColumns().size()));

        for (Row row : spreadsheet.getRows()) {
            out.printf("%-" + rowWidth + "s", row.toString());
            for (Column column : spreadsheet.getColumns()) {
                Object cellValue = spreadsheet.get(row, column);
                out.printf(" | %-" + cellWidth + "s", cellValue == null ? "" : cellValue);
            }
            out.println();
        }
    }
}
