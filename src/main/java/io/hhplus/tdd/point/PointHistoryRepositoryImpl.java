package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository("pointHistoryRepository")
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public PointHistory create(long userId, long amount, TransactionType type) {
        return pointHistoryTable.insert(userId, amount, type, System.currentTimeMillis());
    }

    @Override
    public List<PointHistory> findHistoriesById(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
