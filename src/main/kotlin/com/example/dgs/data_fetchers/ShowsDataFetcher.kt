package com.example.dgs.data_fetchers

import com.example.dgs.generated.types.Show
import com.example.dgs.services.ShowsService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import org.slf4j.LoggerFactory

@DgsComponent
class ShowsDataFetcher(private val showsService: ShowsService) {

  private val logger = LoggerFactory.getLogger(ShowsDataFetcher::class.java)

  @DgsQuery
  fun shows(@InputArgument titleFilter: String): List<Show> {
    logger.info("load shows")

    return showsService.shows().filter { it.title.contains(titleFilter) }
  }

}