package com.example.dgs.services

import com.example.dgs.generated.types.Review
import com.example.dgs.generated.types.SubmittedReview
import com.github.javafaker.Faker
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream
import javax.annotation.PostConstruct

interface ReviewService {
  fun reviewForShows(showIds: List<Int>): Map<Int, List<Review>>
  fun reviewForShows(showId: Int): List<Review>?
  fun saveReview(review: SubmittedReview)
}

@Service
class DefaultReviewService(private val showsService: ShowsService) : ReviewService {

  private val logger = LoggerFactory.getLogger(ReviewService::class.java)

  private val reviews = mutableMapOf<Int, MutableList<Review>>()

  @PostConstruct
  fun createReview() {
    logger.info("start creating reviews")
    val faker = Faker()

    showsService.shows().forEach { show ->
      val generatedReviews = IntStream.range(0, faker.number().numberBetween(1, 10)).mapToObj {
        val date = faker.date().past(300, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        Review(
          username = faker.name().username(),
          starScore = faker.number().numberBetween(0, 6),
          submittedDate = OffsetDateTime.of(date, ZoneOffset.UTC)
        )
      }.toList().toMutableList()

      reviews[show.id] = generatedReviews
    }
  }

  override fun reviewForShows(showIds: List<Int>): Map<Int, List<Review>> {
    logger.info("Retrieving reviews for shows ${showIds.joinToString()}")

    return reviews.filter { showIds.contains(it.key) }
  }

  override fun reviewForShows(showId: Int): List<Review>? {
    return reviews[showId]
  }

  override fun saveReview(review: SubmittedReview) {
    val reviewsForMovie = reviews.getOrPut(review.showId) { mutableListOf() }
    val newReview = Review(
      username = review.username,
      starScore = review.starScore,
      submittedDate = OffsetDateTime.now(ZoneOffset.UTC)
    )

    reviewsForMovie.add(newReview)

    logger.info("review added {}", newReview)
  }

}