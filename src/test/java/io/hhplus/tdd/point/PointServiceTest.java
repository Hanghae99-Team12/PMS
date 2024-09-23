package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PointServiceTest {

    PointService pointService;
    UserPointTable userPointTable;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointService = new PointService(userPointTable);
    }

    @Test
    @DisplayName("특정 유저 포인트 충전")
    void charge() {
        long userId = 1L;
        UserPoint userPoint = pointService.charge(userId, 1000L);
        UserPoint findUserPoint = userPointTable.selectById(userId);

        assertThat(userPoint.isSamePoint(findUserPoint)).isTrue();
    }
}