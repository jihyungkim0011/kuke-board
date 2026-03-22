package kuke.board.article.api;

import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    // app을 실행한 상태에서 테스트를 실행해야한다.
    @Test
    void createTest() {
        ArticleResponse response = create(new ArticleCreateRequest(
                "hi", "my content", 1L, 1L
        ));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request) {
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }


    @Test
    void readTest() {
        ArticleResponse response = read(289292648126451712L);
        System.out.println("response = " + response);
    }

    ArticleResponse read(Long articleId) {
        return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
    }


    @Test
    void updateTest() {
        update(289292648126451712L);
        ArticleResponse response = read(289292648126451712L);
        System.out.println("response = " + response);
    }

    void update(Long articleId) {
        restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 22", "my content 22"))
                .retrieve();
    }

    @Test
    void deleteTest() {
        restClient.delete()
                .uri("/v1/articles/{articleId}", 289292648126451712L)
                .retrieve();
    }

    @Test
    void readAllTest() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScrollTest() {
        List<ArticleResponse> articles1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for (ArticleResponse articleResponse : articles1) {
            System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }

        Long lastArticleId = articles1.getLast().getArticleId();
        List<ArticleResponse> articles2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=%s".formatted(lastArticleId))
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("secondPage");
        for (ArticleResponse articleResponse : articles2) {
            System.out.println("articleResponse.getArticleId() = " + articleResponse.getArticleId());
        }
    }

    @Test
    void countTest() {
        ArticleResponse response = create(new ArticleCreateRequest("hi", "content", 1L, 2L));

        Long count1 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count1 = " + count1); // 1
        assertThat(count1).isEqualTo(1L);


        restClient.delete()
                .uri("/v1/articles/{articleId}", response.getArticleId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
        assertThat(count2).isEqualTo(0L);
    }


    @Getter
    @AllArgsConstructor // 메인에는 생성자 필요없으나 테스트시에만 사용하기 위해 클래스 별도 지정
    static class ArticleCreateRequest {
        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    static class ArticleUpdateRequest {
        private String title;
        private String content;
    }
}
