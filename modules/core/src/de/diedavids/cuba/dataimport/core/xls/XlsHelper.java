package de.diedavids.cuba.dataimport.core.xls;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Resources;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by aleksey on 18/10/2016.
 *
 * Class helping to work with .xls and .xlsx files
 *
 */
public class XlsHelper {
    protected static final char NON_BREAKING_SPACE = (char) 160;
    protected static Resources resources = AppBeans.get(Resources.NAME);

    /**
     * Method returns workbook for .xls and .xlsx files
     */
    @Nullable
    public static Workbook openWorkbook(@Nullable String path) throws IOException {
        Workbook workbook = null;
        InputStream is = resources.getResourceAsStream(path);
        String extension = FilenameUtils.getExtension(path);
        if (is != null) {
            if ("xls".equals(extension)) {
                workbook = new HSSFWorkbook(is);
            } else if ("xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(is);
            }
            is.close();
        }
        return workbook;
    }

    public static Workbook openWorkbook(byte[] bytes) throws IOException {
        Workbook workbook;
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            workbook = new HSSFWorkbook(new POIFSFileSystem(is));
            is.close();
        } catch (OfficeXmlFileException exc) {
            InputStream is = new ByteArrayInputStream(bytes);
            workbook = new XSSFWorkbook(is);
            is.close();
        }
        return workbook;
    }

    @Nullable
    public static Object getCellValue(Cell cell) throws Exception {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                return null;
            case Cell.CELL_TYPE_STRING:
                String formattedCellValue = cell.getStringCellValue().replace(String.valueOf(NON_BREAKING_SPACE), " ").trim();
                return formattedCellValue.isEmpty() ? null : formattedCellValue;
            case Cell.CELL_TYPE_NUMERIC:
                if (isDateCell(cell)) {
                    return cell.getDateCellValue();
                }

                Double numericCellValue = cell.getNumericCellValue();
                if (!isAlmostInt(numericCellValue))
                    return numericCellValue;
                else {
                    if (numericCellValue > Integer.MAX_VALUE) {
                        Long value = numericCellValue.longValue();
                        return value;
                    } else {
                        Integer value = numericCellValue.intValue();
                        return value;
                    }
                }
            case Cell.CELL_TYPE_FORMULA:
                return getFormulaCellValue(cell);
            default:
                throw new IllegalStateException(String.format("Cell type '%s' is not supported", cell.getCellType()));
        }
    }

    protected static boolean isDateCell(Cell cell) {
        return HSSFDateUtil.isCellDateFormatted(cell);
    }

    protected static boolean isAlmostInt(Double numericCellValue) {
        return Math.abs(numericCellValue - numericCellValue.longValue()) < 1e-10;
    }

    protected static Object getFormulaCellValue(Cell cell) {
        String formattedCellValue;
        try {
            formattedCellValue = cell.getStringCellValue().replace(String.valueOf(NON_BREAKING_SPACE), " ").trim();
        } catch (IllegalStateException e) {
            return "Formula error";
        }
        switch (cell.getCachedFormulaResultType()) {
            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
            case Cell.CELL_TYPE_STRING:
                if (formattedCellValue.isEmpty())
                    return null;
                return formattedCellValue;
            default:
                throw new IllegalStateException(String.format("Formula cell type '%s' is not supported", cell.getCachedFormulaResultType()));
        }
    }

    @Nullable
    public static Object getCellValue(Cell cell, Boolean forceToString) throws Exception {
        if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return null;
        }

        cell.setCellType(Cell.CELL_TYPE_STRING);
        return cell.getStringCellValue().replace(String.valueOf(NON_BREAKING_SPACE), " ").trim();
    }

    @Nullable
    public static <T extends Object> T getParameterValue(Map<String, Object> values, String parameter) {
        if (values.get(parameter) == null) {
            return null;
        }

        return (T) values.get(parameter);
    }

    @Nullable
    public static Double getParameterDoubleValue(Map<String, Object> values, String parameter) {
        if (values.get(parameter) == null) {
            return null;
        }

        return Double.valueOf(values.get(parameter).toString());
    }

    @Nullable
    public static Integer getParameterIntegerValue(Map<String, Object> values, String parameter) {
        Object value = values.get(parameter);
        if (value == null) {
            return null;
        }
        if (value instanceof Number)
            return ((Number) value).intValue();
        return Integer.valueOf(value.toString());
    }

    @Nullable
    public static String getParameterStringValue(Map<String, Object> values, String parameter) {
        if (values.get(parameter) == null) {
            return null;
        }

        return values.get(parameter).toString().trim();
    }

}
