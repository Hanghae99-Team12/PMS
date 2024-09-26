package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Repository("userPointRepository")
@RequiredArgsConstructor
public class UserPointRepositoryImpl implements UserPointRepository {

    private static final Logger log = LoggerFactory.getLogger(UserPointRepositoryImpl.class);

    private final UserPointTable userPointTable;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public UserPoint findById(Long userId) {
        return userPointTable.selectById(userId);
    }

    @Override
    public UserPoint updatePointById(Long userId, long point, TransactionType type) {
        lock.lock();
        try {
            long userPoint = findById(userId).point();
            if (TransactionType.isCharge(type)) {
                return userPointTable.insertOrUpdate(userId, userPoint + point);
            }
            return userPointTable.insertOrUpdate(userId, userPoint - point);
        } finally {
            lock.unlock();
        }
    }
}
