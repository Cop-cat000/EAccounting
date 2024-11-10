package project.EAccounting.exceptions.handling;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import jakarta.persistence.EntityExistsException;

import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;

import project.EAccounting.exceptions.*;

@Component
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment environment) {
        if (ex instanceof IncorrectAccountException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError, ex.getMessage());

        if (ex instanceof IncorrectCredentialsException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError, ex.getMessage());

        if (ex instanceof UserNotFoundException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError, ex.getMessage());

        if (ex instanceof UserNotLoggedException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError, ex.getMessage());

        if (ex instanceof EntityExistsException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError, "User with that name exists");

        if (ex instanceof IncorrectTransactionException)
            return customExceptionHandling(ex, environment, ErrorType.ValidationError,ex.getMessage());

        return null;
    }

    private GraphQLError customExceptionHandling(Throwable ex, DataFetchingEnvironment environment, ErrorType type, String message) {
        return GraphqlErrorBuilder.newError()
                .errorType(type)
                .message(message)
                .path(environment.getExecutionStepInfo().getPath())
                .location(environment.getField().getSourceLocation())
                .build();
    }
}