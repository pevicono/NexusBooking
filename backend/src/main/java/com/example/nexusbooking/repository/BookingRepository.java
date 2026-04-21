package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.Booking;
import com.example.nexusbooking.model.Facility;
import com.example.nexusbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByStartTimeDesc(User user);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.facility = :facility " +
           "AND b.status = 'CONFIRMED' " +
           "AND b.startTime < :endTime AND b.endTime > :startTime " +
           "AND (:excludeId IS NULL OR b.id <> :excludeId)")
    boolean existsOverlap(@Param("facility") Facility facility,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime,
                          @Param("excludeId") Long excludeId);
}
