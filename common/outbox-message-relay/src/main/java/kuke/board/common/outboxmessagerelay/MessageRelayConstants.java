package kuke.board.common.outboxmessagerelay;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 샤드 가정. 샤드개수 4. (임의의 값)
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageRelayConstants {
    public static final int SHARD_COUNT = 4;
}
