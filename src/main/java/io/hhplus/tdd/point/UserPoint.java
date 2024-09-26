package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    private static final long MAX_POINT = 100_000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public static UserPoint of(long id,long point) {
        return new UserPoint(id, point, System.currentTimeMillis());
    }

    public boolean isSamePoint(UserPoint other) {
        return this.point == other.point;
    }

    public boolean isSamePoint(long point) {
        return this.point == point;
    }

    public boolean isBeforeUpdatedAt(long updateMillis) {
        return this.updateMillis > updateMillis;
    }

    public UserPoint plusPoint(long amount) {
        if (isExceedMaxPoint(amount)) {
            throw new IllegalArgumentException();
        }
        return new UserPoint(id, point + amount, System.currentTimeMillis());
    }

    public UserPoint minusPoint(long amount) {
        return new UserPoint(id, point - amount, System.currentTimeMillis());
    }

    private boolean isExceedMaxPoint(long point) {
        return this.point + point > MAX_POINT;
    }
}
