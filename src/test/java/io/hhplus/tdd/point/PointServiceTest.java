package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PointServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PointServiceTest.class);

    @Autowired
    PointService pointService;

    /**
     * Given: 특정 유저 포인트를 충전을 하고,
     * When: 해당 유저 포인트를 조회를 하면,
     * Then: 해당 유저의 포인트가 충정액 만큼 증가한다.
     */
    @Test
    @DisplayName("특정 유저 포인트 충전한다.")
    void charge() {
        // Given
        long userId = 1L;
        long point = 5000L;
        pointService.charge(userId, point);

        // When
        UserPoint userPoint = pointService.showPoint(userId);

        // Then
        assertThat(userPoint.point()).isEqualTo(point);
    }
}