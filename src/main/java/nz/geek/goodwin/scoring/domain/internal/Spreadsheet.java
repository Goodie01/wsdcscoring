package nz.geek.goodwin.scoring.domain.internal;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Spreadsheet<Row, Column, CellValue> {
    private final Set<Row> rows;
    private final Set<Column> columns;
    private final Map<CompositeKey<Row, Column>, CellValue> entries;

    public Spreadsheet() {
        rows = new HashSet<>();
        columns = new HashSet<>();
        entries = new HashMap<>();
    }

    public Spreadsheet(final Spreadsheet<Row, Column, CellValue> other) {
        this.rows = new HashSet<>(other.rows);
        this.columns = new HashSet<>(other.columns);

        this.entries = new HashMap<>();
        other.entries.forEach((k, v) -> this.put(k.row, k.column, v));
    }

    public void addRows(Row rows) {
        this.rows.add(rows);
    }

    public void addColumns(Column column) {
        this.columns.add(column);
    }

    public void addRows(Set<Row> rows) {
        this.rows.addAll(rows);
    }

    public void addColumns(Set<Column> columns) {
        this.columns.addAll(columns);
    }

    public Set<Row> getRows() {
        return rows;
    }

    public Set<Column> getColumns() {
        return columns;
    }

    public CellValue get(Row row, Column column) {
        return entries.get(new CompositeKey<>(row, column));
    }

    public Map<Column, CellValue> getAllForRow(Row row) {
        return entries.entrySet().stream()
                .filter(entry -> entry.getKey().row().equals(row))
                .collect(Collectors.toMap(entry -> entry.getKey().column(), Map.Entry::getValue));
    }

    public Map<Row, CellValue> getAllForColumn(Column column) {
        return entries.entrySet().stream()
                .filter(entry -> entry.getKey().column().equals(column))
                .collect(Collectors.toMap(entry -> entry.getKey().row(), Map.Entry::getValue));
    }

    public void put(Row row, Column column, CellValue value) {
        if (!rows.contains(row) || !columns.contains(column)) {
            throw new IllegalArgumentException("Invalid row or column");
        }

        entries.put(new CompositeKey<>(row, column), value);
    }

    private record CompositeKey<Rows, Columns>(Rows row, Columns column) {
    }

    public void output(final Comparator<Column> columnsComparator,
                       final Comparator<Row> rowsComparator,
                       final Function<Column, String> colDisplay,
                       final Function<Row, String> rowDisplay) {
        List<Column> columns = getColumns().stream()
                .distinct()
                .sorted(columnsComparator)
                .toList();

        List<Row> rows = getRows().stream()
                .distinct()
                .sorted(rowsComparator)
                .toList();

        int rowWidth = Math.max(8, rows.stream()
                .mapToInt(d -> rowDisplay.apply(d).length())
                .max()
                .orElse(8));

        int cellWidth = Math.max(8, columns.stream()
                .mapToInt(j -> colDisplay.apply(j).length())
                .max()
                .orElse(8));

        System.out.printf("%-" + rowWidth + "s", "Dancer");

        for (Column column : columns) {
            System.out.printf(" | %-" + cellWidth + "s", colDisplay.apply(column));
        }
        System.out.println();

        System.out.println("-".repeat(rowWidth + (cellWidth + 3) * columns.size()));

        for (Row row : rows) {
            System.out.printf("%-" + rowWidth + "s", rowDisplay.apply(row));
            for (Column column : columns) {
                CellValue cellValue = get(row, column);
                System.out.printf(" | %-" + cellWidth + "s", cellValue == null ? "" : cellValue);
            }
            System.out.println();
        }
    }
}
