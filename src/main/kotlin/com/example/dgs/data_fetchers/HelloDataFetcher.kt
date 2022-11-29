package com.example.dgs.data_fetchers

import com.example.dgs.exceptions.MyException
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsEnableDataFetcherInstrumentation
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class HelloDataFetcher {

  @DgsQuery
  @DgsEnableDataFetcherInstrumentation(true)
  fun hello(): String {
//    throw RuntimeException("oh no")
    throw MyException("oh no")
  }


}