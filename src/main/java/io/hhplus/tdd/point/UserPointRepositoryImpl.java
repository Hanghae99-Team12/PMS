package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository("userPointRepository")
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private final UserPointTable userPointTable;

    @Override
    public UserPoint findById(Long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public UserPoint updatePointById(Long userId, long point) {
        return userPointTable.insertOrUpdate(userId, point);
    }
}
