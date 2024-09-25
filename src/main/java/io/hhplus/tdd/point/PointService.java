package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private static final Logger log = LoggerFactory.getLogger(PointService.class);

    private final UserPointRepository pointRepository;

    public UserPoint charge(long userId, long amount) {
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
        return pointRepository.updatePointById(userId, userPoint.point() - amount);
    }
}
