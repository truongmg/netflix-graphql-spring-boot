package com.example.dgs.data_loaders

import com.example.dgs.generated.types.Review
import com.example.dgs.services.ReviewService
import com.netflix.graphql.dgs.DgsDataLoader
import org.dataloader.MappedBatchLoader
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "reviews")
class ReviewsDataLoader(private val reviewService: ReviewService): MappedBatchLoader<Int, List<Review>> {

  private val logger = LoggerFactory.getLogger(ReviewsDataLoader::class.java)

  override fun load(keys: MutableSet<Int>): CompletionStage<Map<Int, List<Review>>> {
    logger.info("Show ids grouped as ($keys) -> trigger retrieve batch data")

    return CompletableFuture.supplyAsync { reviewService.reviewForShows(keys.stream().toList()) }
  }


}