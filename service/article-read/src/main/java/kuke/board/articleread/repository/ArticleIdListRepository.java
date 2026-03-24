package kuke.board.articleread.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.redis.connection.RedisZSetCommands.*;

@Repository
@RequiredArgsConstructor
public class ArticleIdListRepository {
    private final StringRedisTemplate redisTemplate;

        // article-read::board::{boardId}::article-list
    private static final String KEY_FORMAT = "article-read::board::%s::article-list";

    public void add(Long boardId, Long articleId, Long limit) {
        redisTemplate.executePipelined((RedisCallback<?>) action -> {
            StringRedisConnection conn = (StringRedisConnection) action;
            String key = generateKey(boardId);
            conn.zAdd(key, 0, toPaddedString(articleId)); // 스코어 동일하니까 value(여기서는 articleId) 가 정렬된다.
            conn.zRemRange(key, 0, - limit - 1);
            return null;
        });
    }

    public void delete(Long boardId, Long articleId) {
        redisTemplate.opsForZSet().remove(generateKey(boardId), toPaddedString(articleId));
    }

    public List<Long> readAll(Long boardId, Long offset, Long limit) {
        return redisTemplate.opsForZSet()
                .reverseRange(generateKey(boardId), offset, offset + limit - 1)
                .stream().map(Long::valueOf).toList();
    }

    public List<Long> readAllInfiniteScroll(Long boardId, Long lastArticleId, Long limit) {
        return redisTemplate.opsForZSet().reverseRangeByLex( // value 문자열 내림차순 정렬
                generateKey(boardId),
                // 6,5,4,3,2,1 중 마지막 게시글Id = 4라면
                // 6 5 4 제외 3 2 1 보여준다
                lastArticleId == null ?
                        Range.unbounded() :
                        Range.leftUnbounded(Range.Bound.exclusive(toPaddedString(lastArticleId))),
                Limit.limit().count(limit.intValue())
        ).stream().map(Long::valueOf).toList();
    }

    private String toPaddedString(Long articleId) {
        return "%019d".formatted(articleId);
        // 1234 -> 0000000000000001234
    }

    private String generateKey(Long boardId) {
        return KEY_FORMAT.formatted(boardId);
    }
}
