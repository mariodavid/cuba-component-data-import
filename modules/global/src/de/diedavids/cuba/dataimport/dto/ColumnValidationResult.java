package de.diedavids.cuba.dataimport.dto;

import java.util.List;

public class ColumnValidationResult {
    public ColumnValidationResult(boolean valid, List<String> columns) {
        this.valid = valid;
        this.columns = columns;
    }

    public boolean getValid() {
        return valid;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    private boolean valid;
    private List<String> columns;
}
