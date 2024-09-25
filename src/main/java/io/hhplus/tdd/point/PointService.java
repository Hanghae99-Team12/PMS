package io.hhplus.tdd.point;

import java.util.List;
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
        pointHistoryRepository.create(userId, amount, TransactionType.CHARGE);
        return pointRepository.updatePointById(userId, amount);
    }

    public UserPoint showPoint(long id) {
        return pointRepository.findById(id);
    }

    public UserPoint use(long userId, long amount) {
        UserPoint userPoint = showPoint(userId);
        if (userPoint.point() < amount) {
            throw new IllegalArgumentException("보유한 포인트보다 더 사용할 수 없습니다.");
        }
        pointHistoryRepository.create(userId, amount, TransactionType.USE);
        return pointRepository.updatePointById(userId, userPoint.point() - amount);
    }

    public List<PointHistory> showHistories(long userId) {
        return pointHistoryRepository.findHistoriesById(userId);
    }
}
