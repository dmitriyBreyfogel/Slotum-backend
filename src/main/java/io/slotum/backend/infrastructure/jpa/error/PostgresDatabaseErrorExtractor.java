package io.slotum.backend.infrastructure.jpa.error;

import org.hibernate.JDBCException;
import org.hibernate.exception.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@Component
public final class PostgresDatabaseErrorExtractor implements DatabaseErrorExtractor {

    @Override
    public DatabaseErrorDetails extract(Throwable throwable) {
        DatabaseErrorDetails result = DatabaseErrorDetails.empty();
        Set<Throwable> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        Throwable current = throwable;

        while (current != null && visited.add(current)) {
            result = extractOne(current).merge(result);

            if (current instanceof JDBCException jdbcException) {
                result = extractSqlExceptionChain(
                        jdbcException.getSQLException(),
                        result,
                        visited
                );
            }

            if (current instanceof SQLException sqlException) {
                result = extractSqlExceptionChain(
                        sqlException.getNextException(),
                        result,
                        visited
                );
            }

            current = current.getCause();
        }

        return result;
    }

    private DatabaseErrorDetails extractOne(Throwable throwable) {
        if (throwable instanceof PSQLException exception) {
            return extractFromPostgresException(exception);
        }

        if (throwable instanceof ConstraintViolationException exception) {
            return extractFromHibernateConstraintViolationException(exception);
        }

        if (throwable instanceof JDBCException exception) {
            return extractFromHibernateJdbcException(exception);
        }

        if (throwable instanceof SQLException exception) {
            return extractFromSqlException(exception);
        }

        return extractFromGenericThrowable(throwable);
    }

    private DatabaseErrorDetails extractSqlExceptionChain(
            SQLException exception,
            DatabaseErrorDetails result,
            Set<Throwable> visited
    ) {
        SQLException current = exception;

        while (current != null && visited.add(current)) {
            result = extractOne(current).merge(result);
            current = current.getNextException();
        }

        return result;
    }

    private DatabaseErrorDetails extractFromPostgresException(PSQLException exception) {
        DatabaseErrorDetails sqlDetails = extractFromSqlException(exception);

        ServerErrorMessage serverErrorMessage = exception.getServerErrorMessage();

        if (serverErrorMessage == null) {
            return sqlDetails;
        }

        DatabaseErrorDetails postgresDetails = new DatabaseErrorDetails(
                serverErrorMessage.getMessage(),
                exception.getSQLState(),
                exception.getErrorCode(),
                serverErrorMessage.getConstraint(),
                serverErrorMessage.getSchema(),
                serverErrorMessage.getTable(),
                serverErrorMessage.getColumn(),
                serverErrorMessage.getDetail(),
                serverErrorMessage.getHint()
        );

        return postgresDetails.merge(sqlDetails);
    }

    private DatabaseErrorDetails extractFromHibernateConstraintViolationException(
            ConstraintViolationException exception
    ) {
        return new DatabaseErrorDetails(
                exception.getMessage(),
                exception.getSQLState(),
                exception.getErrorCode(),
                exception.getConstraintName(),
                null,
                null,
                null,
                null,
                null
        );
    }

    private DatabaseErrorDetails extractFromHibernateJdbcException(JDBCException exception) {
        return new DatabaseErrorDetails(
                exception.getMessage(),
                exception.getSQLState(),
                exception.getErrorCode(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private DatabaseErrorDetails extractFromSqlException(SQLException exception) {
        return new DatabaseErrorDetails(
                exception.getMessage(),
                exception.getSQLState(),
                exception.getErrorCode(),
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    private DatabaseErrorDetails extractFromGenericThrowable(Throwable throwable) {
        return new DatabaseErrorDetails(
                throwable == null ? null : throwable.getMessage(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }
}