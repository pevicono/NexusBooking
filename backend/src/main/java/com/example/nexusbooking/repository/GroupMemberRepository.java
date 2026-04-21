package com.example.nexusbooking.repository;

import com.example.nexusbooking.model.Group;
import com.example.nexusbooking.model.GroupMember;
import com.example.nexusbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroup(Group group);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    boolean existsByGroupAndUser(Group group, User user);
    void deleteByGroupAndUser(Group group, User user);
}
