package com.example.dgs.data_fetchers

import com.example.dgs.generated.types.Movie
import com.example.dgs.generated.types.Show
import com.example.dgs.generated.types.Video
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery

@DgsComponent
class VideosDataFetcher {

  private val videos = listOf(
    Show(id = 1, title = "Stranger Things", releaseYear = 2016),
    Movie(title = "Rush Hour", length = 125)
  )

  @DgsQuery
  fun videos(): List<Video> {
    return videos
  }

}