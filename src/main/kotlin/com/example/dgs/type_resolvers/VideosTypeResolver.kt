package com.example.dgs.type_resolvers

import com.example.dgs.generated.types.Movie
import com.example.dgs.generated.types.Show
import com.example.dgs.generated.types.Video
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsTypeResolver
import java.lang.RuntimeException

@DgsComponent
class VideosTypeResolver {

  @DgsTypeResolver(name = "Video")
  fun resolveVideoType(video: Video): String = when (video) {
    is Movie -> {
      "Movie"
    }

    is Show -> {
      "Show"
    }

    else -> {
      throw RuntimeException("Unknown type ${video::class}")
    }
  }

}