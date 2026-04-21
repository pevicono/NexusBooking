package com.example.nexusbooking.service;

import com.example.nexusbooking.dto.GroupRequest;
import com.example.nexusbooking.model.Group;
import com.example.nexusbooking.model.GroupMember;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.GroupMemberRepository;
import com.example.nexusbooking.repository.GroupRepository;
import com.example.nexusbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,
                        GroupMemberRepository groupMemberRepository,
                        UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    public List<Group> findAll() {
        return groupRepository.findAll().stream().map(this::ensureJoinCode).toList();
    }

    public List<Group> findByMember(User user) {
        return groupRepository.findGroupsByMember(user).stream().map(this::ensureJoinCode).toList();
    }

    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    @Transactional
    public Group create(User owner, GroupRequest request) {
        validateUserCanParticipate(owner);
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setOwner(owner);
        group.setJoinCode(generateUniqueJoinCode());
        Group saved = groupRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroup(saved);
        ownerMember.setUser(owner);
        ownerMember.setRole(GroupMember.Role.OWNER);
        groupMemberRepository.save(ownerMember);
        return saved;
    }

    @Transactional
    public Group join(User user, Long groupId) {
        validateUserCanParticipate(user);
        Group group = findById(groupId);
        ensureJoinCode(group);
        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User is already a member of this group");
        }
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(GroupMember.Role.MEMBER);
        groupMemberRepository.save(member);
        return group;
    }

    @Transactional
    public Group joinByCode(User user, String code) {
        validateUserCanParticipate(user);
        if (code == null || code.isBlank()) {
            throw new RuntimeException("Join code is required");
        }
        Group group = groupRepository.findByJoinCode(code.trim().toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new RuntimeException("Group not found for this code"));
        return join(user, group.getId());
    }

    @Transactional
    public void leave(User user, Long groupId) {
        Group group = findById(groupId);
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        if (member.getRole() == GroupMember.Role.OWNER) {
            throw new RuntimeException("Owner cannot leave group. Delete the group instead.");
        }
        groupMemberRepository.delete(member);
    }

    @Transactional
    public void delete(User user, Long groupId) {
        Group group = findById(groupId);
        if (!group.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only owner can delete this group");
        }
        groupRepository.delete(group);
    }

    @Transactional
    public Group update(User user, Long groupId, GroupRequest request) {
        Group group = findById(groupId);
        ensureJoinCode(group);
        if (!group.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only owner can edit this group");
        }
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return groupRepository.save(group);
    }

    @Transactional
    public Group adminUpdate(Long groupId, GroupRequest request) {
        Group group = findById(groupId);
        ensureJoinCode(group);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return groupRepository.save(group);
    }

    @Transactional
    public Group adminCreate(String name, String description, Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        validateUserCanParticipate(owner);

        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        group.setOwner(owner);
        group.setJoinCode(generateUniqueJoinCode());
        Group saved = groupRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroup(saved);
        ownerMember.setUser(owner);
        ownerMember.setRole(GroupMember.Role.OWNER);
        groupMemberRepository.save(ownerMember);
        return saved;
    }

    @Transactional
    public void adminDelete(Long groupId) {
        Group group = findById(groupId);
        groupRepository.delete(group);
    }

    private void validateUserCanParticipate(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Admins cannot be members of groups");
        }
    }

    private Group ensureJoinCode(Group group) {
        if (group.getJoinCode() == null || group.getJoinCode().isBlank()) {
            group.setJoinCode(generateUniqueJoinCode());
            return groupRepository.save(group);
        }
        return group;
    }

    private String generateUniqueJoinCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "")
                    .substring(0, 8)
                    .toUpperCase(Locale.ROOT);
        } while (groupRepository.findByJoinCode(code).isPresent());
        return code;
    }
}
