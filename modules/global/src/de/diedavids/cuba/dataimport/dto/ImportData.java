package de.diedavids.cuba.dataimport.dto;

import java.io.Serializable;
import java.util.List;

public interface ImportData extends Serializable {

    List<DataRow> getRows();
    List<String> getColumns();

}