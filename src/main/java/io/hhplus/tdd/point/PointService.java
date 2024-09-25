package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
}
