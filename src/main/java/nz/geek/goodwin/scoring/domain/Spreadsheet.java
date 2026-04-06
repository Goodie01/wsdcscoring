package nz.geek.goodwin.scoring.domain;

import org.apache.commons.lang3.function.TriConsumer;

import java.util.*;
import java.util.stream.Collectors;

public class Spreadsheet<Row, Column, CellValue> {
    private final List<Row> rows;
    private final List<Column> columns;
    private final Map<CompositeKey<Row, Column>, CellValue> entries;

    public Spreadsheet() {
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        entries = new HashMap<>();
    }

    public Spreadsheet(final Spreadsheet<Row, Column, CellValue> other) {
        this.rows = new ArrayList<>(other.rows);
        this.columns = new ArrayList<>(other.columns);

        this.entries = new HashMap<>();
        other.entries.forEach((k, v) -> this.put(k.row, k.column, v));
    }

    public void addRow(Row row, CellValue... values) {
        this.rows.add(row);

        if (values.length != 0 && values.length != columns.size()) {
            throw new IllegalArgumentException("Invalid number of values");
        }

        for (int i = 0; i < values.length; i++) {
            put(row, columns.get(i), values[i]);
        }
    }

    public void addColumn(Column column, CellValue... values) {
        this.columns.add(column);

        if (values.length != 0 && values.length != rows.size()) {
            throw new IllegalArgumentException("Invalid number of values");
        }

        for (int i = 0; i < values.length; i++) {
            put(rows.get(i), column, values[i]);
        }
    }

    public void addRows(List<Row> rows) {
        this.rows.addAll(rows);
    }

    public void addColumns(List<Column> columns) {
        this.columns.addAll(columns);
    }

    public List<Row> getRows() {
        return rows;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public CellValue get(Row row, Column column) {
        return entries.get(new CompositeKey<>(row, column));
    }

    public void removeRows(final Collection<Row> rows) {
        rows.forEach(this::removeRow);
    }

    public void removeColumns(final Collection<Column> columns) {
        columns.forEach(this::removeColumn);
    }

    public void removeRow(final Row row) {
        if (row == null) {
            return;
        }
        entries.entrySet().removeIf(entry -> entry.getKey().row().equals(row));
        rows.remove(row);
    }

    public void removeColumn(final Column column) {
        if (column == null) {
            return;
        }
        entries.entrySet().removeIf(entry -> entry.getKey().column().equals(column));
        columns.remove(column);
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

    public void forEach(TriConsumer<Row, Column, CellValue> consumer) {
        entries.forEach((k, v) -> consumer.accept(k.row(), k.column(), v));
    }

    private record CompositeKey<Rows, Columns>(Rows row, Columns column) {
    }
}
