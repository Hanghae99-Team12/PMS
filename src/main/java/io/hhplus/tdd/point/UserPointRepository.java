package io.hhplus.tdd.point;

import org.springframework.stereotype.Repository;

interface UserPointRepository {
    UserPoint findById(Long id);
    UserPoint updatePointById(Long id, long point);
}
