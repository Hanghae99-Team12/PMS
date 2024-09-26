package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
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
        long pointOfCharge = 5000L;
        UserPoint userPoint = UserPoint.of(userId, 0L);
        given(userPointRepository.findById(userId))
                .willReturn(userPoint);
        given(userPointRepository.updatePointById(userId, pointOfCharge, TransactionType.CHARGE))
                .willReturn(userPoint.plusPoint(5000L));

        // When
        UserPoint result = pointService.charge(userId, pointOfCharge);

        // Then
        assertThat(result.point()).isEqualTo(pointOfCharge);
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
        Mockito.lenient().when(userPointRepository.findById(userId))
                .thenReturn(UserPoint.of(userId, point));
        Mockito.lenient().when(userPointRepository.updatePointById(userId, 1000L, TransactionType.USE))
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
        Mockito.lenient().when(userPointRepository.updatePointById(userId, point, TransactionType.USE))
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

    /**
     * When: 존재하지 않는 유저를 조회를 하면,
     * Then: 예외가 발생합니다..
     */
    @Test
    @DisplayName("없는 유저의 포인트 조회시 예외 발생")
    void find_non_existent_userPoint_exception() {
        // When & Then
        assertThatThrownBy(() ->pointService.showPoint(0L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * When: 존재하지 않는 유저의 포인트 충전시,
     * Then: 예외가 발생한다.
     */
    @Test
    @DisplayName("존재하지 않는 유저의 포인트 충전시 예외 발생")
    void charge_userPoint_non_existent_user_exception() {
        // When & Then
        assertThatThrownBy(() -> pointService.charge(0L, 5000L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * When: 존재하지 않는 유저의 포인트 사용시시,
     * Then: 예외가 발생한다.
     */
    @Test
    @DisplayName("존재하지 않는 유저의 포인트 사용시 예외 발생")
    void use_userPoint_non_existent_user_exception() {
        // When & Then
        assertThatThrownBy(() -> pointService.use(0L, 5000L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * When: 해당 유저가 100,000 초과 충전하면,
     * Then: 예외가 발생한다.
     */
    @Test
    @DisplayName("유저가 포인트를 충전 할때 최대 잔고를 초과를 하면 예외가 발생한다.")
    void throws_exception_if_userPoint_exceed_limit() {
        // When & Then
        assertThatThrownBy(() -> pointService.charge(0L, 100_001L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}