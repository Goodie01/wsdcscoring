package nz.geek.goodwin.scoring.internal.spreadsheet;

import nz.geek.goodwin.scoring.domain.Spreadsheet;

import java.io.PrintStream;
import java.util.Objects;

public class HtmlPrinter {
    private final PrintStream out;

    public HtmlPrinter(PrintStream out) {
        this.out = Objects.requireNonNull(out, "out");
    }

    public <Row, Column, CellValue> void print(Spreadsheet<Row, Column, CellValue> spreadsheet) {
        print(spreadsheet, null);
    }

    public <Row, Column, CellValue> void print(Spreadsheet<Row, Column, CellValue> spreadsheet, String title) {
        out.println("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <style>
                    body {
                      font-family: Arial, Helvetica, sans-serif;
                      margin: 16px;
                      color: #000;
                    }

                    .title {
                      font-weight: bold;
                      margin: 0 0 10px 0;
                    }

                    table.sheet {
                      border-collapse: collapse;
                      border: 2px solid #000;
                      font-size: 14px;
                    }

                    table.sheet th,
                    table.sheet td {
                      border: 1px solid #000;
                      padding: 6px 10px;
                      text-align: center;
                      vertical-align: middle;
                      white-space: nowrap;
                    }

                    table.sheet thead th {
                      background: #d9d9d9;
                      font-weight: bold;
                    }

                    table.sheet tbody th {
                      background: #efefef;
                      font-weight: bold;
                      text-align: left;
                    }

                    table.sheet th.corner {
                      background: #d9d9d9;
                    }
                  </style>
                </head>
                <body>
                """);

        if (title != null && !title.isBlank()) {
            out.println("  <div class=\"title\">" + escapeHtml(title) + "</div>");
        }

        out.println("""
                  <table class="sheet">
                    <thead>
                      <tr>
                        <th class="corner"></th>
                """);

        for (Column column : spreadsheet.getColumns()) {
            out.println("        <th>" + escapeHtml(column) + "</th>");
        }

        out.println("""
                      </tr>
                    </thead>
                    <tbody>
                """);

        for (Row row : spreadsheet.getRows()) {
            out.println("      <tr>");
            out.println("        <th>" + escapeHtml(row) + "</th>");

            for (Column column : spreadsheet.getColumns()) {
                CellValue value = spreadsheet.get(row, column);
                out.println("        <td>" + escapeHtml(value) + "</td>");
            }

            out.println("      </tr>");
        }

        out.println("""
                    </tbody>
                  </table>
                </body>
                </html>
                """);
    }

    private static String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }

        String text = String.valueOf(value);
        StringBuilder escaped = new StringBuilder(text.length() + 16);

        for (char c : text.toCharArray()) {
            switch (c) {
                case '&' -> escaped.append("&amp;");
                case '<' -> escaped.append("&lt;");
                case '>' -> escaped.append("&gt;");
                case '"' -> escaped.append("&quot;");
                case '\'' -> escaped.append("&#39;");
                default -> escaped.append(c);
            }
        }

        return escaped.toString();
    }
}
