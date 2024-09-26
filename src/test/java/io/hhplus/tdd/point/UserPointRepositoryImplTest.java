package io.hhplus.tdd.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import io.hhplus.tdd.database.UserPointTable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


class UserPointRepositoryImplTest {

    private final UserPointTable userPointTable = new UserPointTable();
    private UserPointRepository userPointRepository;

    @BeforeEach
    void setUp() {
        userPointRepository = new UserPointRepositoryImpl(userPointTable);
    }

    /**
     * Given: 유저가 존재하고
     * When: 포인트를 여러번 충전 했을때,
     * Then: 순차적으로 포인트가 충전이 된다.
     */
    @Test
    @DisplayName("포인트 변경 동시성 테스트")
    void test_change_point_concurrency() throws InterruptedException {
        // Given
        int numberOfThreads = 5;
        long userId = 1L;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // When
        service.execute(() -> {
            userPointRepository.updatePointById(userId, 1000L, TransactionType.CHARGE);
            latch.countDown();
        });
        service.execute(() -> {
            userPointRepository.updatePointById(userId, 2000L, TransactionType.CHARGE);
            latch.countDown();
        });
        service.execute(() -> {
            userPointRepository.updatePointById(userId, 3000L, TransactionType.USE);
            latch.countDown();
        });
        service.execute(() -> {
            userPointRepository.updatePointById(userId, 4000L, TransactionType.CHARGE);
            latch.countDown();
        });
        service.execute(() -> {
            userPointRepository.updatePointById(userId, 5000L, TransactionType.CHARGE);
            latch.countDown();
        });
        latch.await();

        // Then
        UserPoint userPoint = userPointRepository.findById(userId);
        assertThat(userPoint.point()).isEqualTo(9000L);
    }
}