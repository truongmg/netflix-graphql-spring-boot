package com.example.dgs.data_fetchers

import com.example.dgs.data_loaders.ReviewsDataLoader
import com.example.dgs.generated.client.AddReviewGraphQLQuery
import com.example.dgs.generated.client.AddReviewProjectionRoot
import com.example.dgs.generated.client.ShowsGraphQLQuery
import com.example.dgs.generated.client.ShowsProjectionRoot
import com.example.dgs.generated.types.Review
import com.example.dgs.generated.types.Show
import com.example.dgs.generated.types.SubmittedReview
import com.example.dgs.scalars.DateTimeScalarRegistration
import com.example.dgs.services.ReviewService
import com.example.dgs.services.ShowsService
import com.jayway.jsonpath.TypeRef
import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.time.OffsetDateTime

@SpringBootTest(
  classes = [
    DgsAutoConfiguration::class, ShowsDataFetcher::class, DateTimeScalarRegistration::class,
    ReviewsDataFetcher::class, ReviewsDataLoader::class
  ]
)
class ShowsDataFetcherTest {

  @Autowired
  lateinit var dgsQueryExecutor: DgsQueryExecutor

  @MockBean
  lateinit var showsService: ShowsService

  @MockBean
  lateinit var reviewService: ReviewService

  @BeforeEach
  fun before() {
    `when`(showsService.shows()).thenAnswer {
      listOf(Show(id = 1, title = "mock title", releaseYear = 2020))
    }
    `when`(reviewService.reviewForShows(listOf(1))).thenAnswer {
      mapOf(
        Pair(
          1, listOf(
            Review(username = "User 1", starScore = 5, submittedDate = OffsetDateTime.now()),
            Review(username = "User 2", starScore = 2, submittedDate = OffsetDateTime.now())
          )
        )
      )
    }
  }

  @Test
  fun queryShowsTest() {
    val query =
      """
        query {
          shows(titleFilter: "") {
            id
            title
            releaseYear
          }
        }
      """.trimIndent()

    val titles : List<String> = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.shows[*].title")
    assertThat(titles).contains("mock title")
  }

  @Test
  fun queryShowsWithQueryApiTest() {
    val request = GraphQLQueryRequest(
      ShowsGraphQLQuery.Builder()
        .titleFilter("")
        .build(),
      ShowsProjectionRoot()
        .title()
    )

    val titles = dgsQueryExecutor.executeAndExtractJsonPath<List<String>>(
      request.serialize(),
      "data.shows[*].title"
    )

    assertThat(titles[0]).contains("mock title")
  }

  @Test
  fun queryShowsWithReviewsTest() {
    val request = GraphQLQueryRequest(
      ShowsGraphQLQuery.Builder()
        .titleFilter("")
        .build(),
      ShowsProjectionRoot()
        .title()
        .releaseYear()
        .reviews()
          .username()
          .submittedDate()
          .starScore()
    )

    val shows = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
      request.serialize(),
      "data.shows[*]",
      object : TypeRef<List<Show>>() {}
    )

    assertThat(shows.size).isEqualTo(1)
    assertThat(shows[0].reviews?.size).isEqualTo(2)
  }

  @Test
  fun addReviewTest() {
    val request = GraphQLQueryRequest(
      AddReviewGraphQLQuery.Builder()
        .review(
          SubmittedReview(showId = 1, username = "User 1", 4)
        )
        .build(),
      AddReviewProjectionRoot()
        .username()
        .starScore()
        .submittedDate()
    )

    val result = dgsQueryExecutor.execute(request.serialize())
    assertThat(result.errors).isEmpty()

    verify(reviewService).reviewForShows(1)
  }
}