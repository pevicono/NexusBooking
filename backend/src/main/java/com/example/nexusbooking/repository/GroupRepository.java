package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.Group;
import com.example.nexusbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByOwner(User owner);
    Optional<Group> findByJoinCode(String joinCode);

    @Query("SELECT g FROM BookingGroup g JOIN g.members m WHERE m.user = :user")
    List<Group> findGroupsByMember(@Param("user") User user);
}
