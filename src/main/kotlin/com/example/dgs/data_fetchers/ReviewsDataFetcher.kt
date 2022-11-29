package com.example.dgs.data_fetchers

import com.example.dgs.data_loaders.ReviewsDataLoader
import com.example.dgs.generated.DgsConstants
import com.example.dgs.generated.types.Review
import com.example.dgs.generated.types.Show
import com.example.dgs.generated.types.SubmittedReview
import com.example.dgs.services.ReviewService
import com.netflix.graphql.dgs.*
import graphql.execution.DataFetcherResult
import org.dataloader.DataLoader
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

@DgsComponent
class ReviewsDataFetcher(private val reviewService: ReviewService) {

  private val logger = LoggerFactory.getLogger(ReviewsDataFetcher::class.java)

  @DgsData(parentType = DgsConstants.SHOW.TYPE_NAME)
  fun reviews(dfe: DgsDataFetchingEnvironment): CompletableFuture<DataFetcherResult<List<Review>>> {
    val show: Show = dfe.getSource()
    val reviewsDataLoader: DataLoader<Int, List<Review>> = dfe.getDataLoader(ReviewsDataLoader::class.java)

    logger.info("load reviews called for show ${show.id}")
    val reviews = reviewsDataLoader.load(show.id)
    return reviews.thenApply { list -> DataFetcherResult.newResult<List<Review>>().data(list).localContext(show).build() }
  }

  @DgsMutation
  fun addReview(@InputArgument review: SubmittedReview): List<Review> {
    reviewService.saveReview(review)

    return reviewService.reviewForShows(review.showId)?: emptyList()
  }

  @DgsData(parentType = DgsConstants.REVIEW.TYPE_NAME)
  fun starScore(dfe: DgsDataFetchingEnvironment): Int {
    val show = dfe.getLocalContext<Show>()
    val review = dfe.getSource<Review>()
    if (show != null && show.title == "Ozark") {
      return 3;
    }
    return review.starScore?: 0
  }

}