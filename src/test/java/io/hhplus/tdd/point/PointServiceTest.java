package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private static final Logger log = LoggerFactory.getLogger(PointServiceTest.class);

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    PointService pointService;

    @BeforeEach
    void setUp() {
        pointService = new PointService(userPointRepository, pointHistoryRepository);
    }

    /**
     * Given: 특정 유저 포인트를 충전을 하고,
     * When: 해당 유저 포인트를 조회를 하면,
     * Then: 해당 유저의 포인트가 충전액 만큼 증가한다.
     */
    @Test
    @DisplayName("특정 유저 포인트 충전한다.")
    void charge() {
        // Given
        long userId = 1L;
        long point = 5000L;
        Mockito.lenient().when(userPointRepository.updatePointById(userId, point))
                .thenReturn(UserPoint.of(userId, point));
        Mockito.lenient().when(userPointRepository.findById(userId))
                .thenReturn(UserPoint.of(userId, point));

        // When
        UserPoint userPoint = pointService.showPoint(userId);

        // Then
        assertThat(userPoint.point()).isEqualTo(point);
    }

    /**
     * Given: 특정 유저 포인트를 충전을 하고,
     * When: 해당 유저 포인트를 사용하면,
     * Then: 해당 유저의 포인트가 사용한 양을 제외한 포인트가 남는다.
     */
    @Test
    @DisplayName("특정 유저 포인트 사용한다.")
    void use() {
        // Given
        long userId = 1L;
        long point = 5000L;
        long remainOfPoint = 4000L;
        Mockito.lenient().when(userPointRepository.updatePointById(userId, point))
                .thenReturn(UserPoint.of(userId, point));
        Mockito.lenient().when(userPointRepository.findById(userId))
                .thenReturn(UserPoint.of(userId, point));
        Mockito.lenient().when(userPointRepository.updatePointById(userId, remainOfPoint))
                .thenReturn(UserPoint.of(userId, remainOfPoint));

        // When
        UserPoint userPoint = pointService.use(userId, 1000L);

        // Then
        assertThat(userPoint.point()).isEqualTo(4000L);
    }

    /**
     * Given: 특정 유저 포인트를 충전을 하고,
     * When: 해당 유저가 가진 포인트를 더 사용하면,
     * Then: 예외가 발생한다.
     */
    @Test
    @DisplayName("특정 유저가 보유 포인트보다 더 사용하면 예외가 발생한다.")
    void useException() {
        // Given
        long userId = 1L;
        long point = 5000L;
        Mockito.lenient().when(userPointRepository.updatePointById(userId, point))
                .thenReturn(UserPoint.of(userId, point));
        Mockito.lenient().when(userPointRepository.findById(userId))
                .thenReturn(UserPoint.of(userId, point));

        // When & Then
        assertThatThrownBy(() -> pointService.use(userId, 6000L)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * Given: 특정 유저 포인트 내역을 가지고있고,
     * When: 특정 유저의 포인트 내역을 조회하면,
     * Then: 특정 유저의 포인트 내역 목록이 반환된다.
     */
    @Test
    @DisplayName("특정 유저의 히스토리를 확인 할 수 있다.")
    void showHistory() {
        // Given & When
        long userId = 1L;
        long firstPoint = 5000L;
        PointHistory firstHistory = new PointHistory(userId, firstPoint, TransactionType.CHARGE);

        long secondPoint = 4000L;
        PointHistory secondHistory = new PointHistory(userId, secondPoint, TransactionType.CHARGE);

        long usePoint = 1000L;
        PointHistory thirdHistory = new PointHistory(userId, usePoint, TransactionType.USE);

        Mockito.lenient().when(pointHistoryRepository.findHistoriesById(userId))
                .thenReturn(List.of(firstHistory, secondHistory, thirdHistory));

        // Then
        List<PointHistory> pointHistories = pointService.showHistories(userId);
        assertThat(pointHistories).containsExactlyInAnyOrder(firstHistory, secondHistory, thirdHistory);
    }
}