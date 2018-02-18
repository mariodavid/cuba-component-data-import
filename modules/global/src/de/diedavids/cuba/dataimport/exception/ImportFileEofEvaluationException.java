/*
 * TODO Copyright
 */

package de.diedavids.cuba.dataimport.exception;

/**
 * Created by aleksey on 21/10/2016.
 */
public class ImportFileEofEvaluationException extends Exception {
    public ImportFileEofEvaluationException() {
    }

    public ImportFileEofEvaluationException(String message) {
        super(message);
    }

    public ImportFileEofEvaluationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportFileEofEvaluationException(Throwable cause) {
        super(cause);
    }

    public ImportFileEofEvaluationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
