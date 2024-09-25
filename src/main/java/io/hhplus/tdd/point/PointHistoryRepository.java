package io.hhplus.tdd.point;

import java.util.List;

interface PointHistoryRepository {
    PointHistory create(long userId, long amount, TransactionType type);
    List<PointHistory> findHistoriesById(long userId);
}
