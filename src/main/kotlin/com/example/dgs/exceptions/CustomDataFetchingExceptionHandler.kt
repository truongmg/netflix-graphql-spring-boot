package com.example.dgs.exceptions

import com.netflix.graphql.types.errors.TypedGraphQLError
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class CustomDataFetchingExceptionHandler: DataFetcherExceptionHandler {

  override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters?): CompletableFuture<DataFetcherExceptionHandlerResult> {
    if (handlerParameters?.exception is MyException) {
      val debugInfo = mapOf(
        Pair("somefield", "somevalue"),
        Pair("stack trace", handlerParameters.exception.stackTrace)
      )

      val graphQLError = TypedGraphQLError.newInternalErrorBuilder()
        .message("This custom thing went wrong!")
        .debugInfo(debugInfo)
        .path(handlerParameters.path)
        .build()

      val result = DataFetcherExceptionHandlerResult.newResult()
        .error(graphQLError)
        .build()

      return CompletableFuture.completedFuture(result)
    }

    return super.handleException(handlerParameters)
  }
}