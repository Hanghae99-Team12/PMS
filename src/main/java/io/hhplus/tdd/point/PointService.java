package io.hhplus.tdd.point;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final UserPointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public UserPoint charge(long userId, long amount) {
        UserPoint userPoint = showPoint(userId);
        UserPoint userPointOfCharge = userPoint.plusPoint(amount);
        pointHistoryRepository.create(userId, userPointOfCharge.point(), TransactionType.CHARGE);
        return pointRepository.updatePointById(userId, userPointOfCharge.point(), TransactionType.CHARGE);
    }

    public UserPoint showPoint(long id) {
        UserPoint findUserPoint = pointRepository.findById(id);
        if (Objects.isNull(findUserPoint)) {
            throw new IllegalArgumentException("해당 유저는 존재하지 않습니다.");
        }
        return pointRepository.findById(id);
    }

    public UserPoint use(long userId, long amount) {
        UserPoint userPoint = showPoint(userId);
        if (userPoint.point() < amount) {
            throw new IllegalArgumentException("보유한 포인트보다 더 사용할 수 없습니다.");
        }
        pointHistoryRepository.create(userId, amount, TransactionType.USE);
        UserPoint useUserPoint = userPoint.minusPoint(amount);
        return pointRepository.updatePointById(userId, useUserPoint.point(), TransactionType.USE);
    }

    public List<PointHistory> showHistories(long userId) {
        return pointHistoryRepository.findHistoriesById(userId);
    }
}
