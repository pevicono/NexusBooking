package com.example.nexusbooking.service;

import com.example.nexusbooking.dto.BookingRequest;
import com.example.nexusbooking.model.Booking;
import com.example.nexusbooking.model.Facility;
import com.example.nexusbooking.model.Group;
import com.example.nexusbooking.model.User;
import com.example.nexusbooking.repository.BookingRepository;
import com.example.nexusbooking.repository.FacilityRepository;
import com.example.nexusbooking.repository.GroupMemberRepository;
import com.example.nexusbooking.repository.GroupRepository;
import com.example.nexusbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FacilityRepository facilityRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          FacilityRepository facilityRepository,
                          GroupRepository groupRepository,
                          GroupMemberRepository groupMemberRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.facilityRepository = facilityRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> findByUser(User user) {
        return bookingRepository.findByUserOrderByStartTimeDesc(user);
    }

    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Booking findById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking create(User user, BookingRequest request) {
        validateBookerUser(user);
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        Facility facility = facilityRepository.findById(request.getFacilityId())
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        if (facility.getStatus() != Facility.Status.ACTIVE) {
            throw new RuntimeException("Facility is not available");
        }

        boolean overlap = bookingRepository.existsOverlap(
                facility,
                request.getStartTime(),
                request.getEndTime(),
                null
        );
        if (overlap) {
            throw new RuntimeException("Time slot is not available for this facility");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFacility(facility);
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setNotes(request.getNotes());
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User must be member of the selected group");
        }
        booking.setGroup(group);

        return bookingRepository.save(booking);
    }

    public Booking cancel(User user, Long bookingId) {
        Booking booking = findById(bookingId);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }
        booking.setStatus(Booking.Status.CANCELLED);
        return bookingRepository.save(booking);
    }

    public Booking createForUser(User user, Long facilityId, Long groupId, LocalDateTime startTime, LocalDateTime endTime, String notes) {
        validateBookerUser(user);
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }

        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));

        boolean overlap = bookingRepository.existsOverlap(facility, startTime, endTime, null);
        if (overlap) {
            throw new RuntimeException("Time slot is not available for this facility");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFacility(facility);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setNotes(notes == null ? "" : notes);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new RuntimeException("User must be member of the selected group");
        }
        booking.setGroup(group);
        return bookingRepository.save(booking);
    }

    public Booking update(User user, Long bookingId, BookingRequest request) {
        validateBookerUser(user);
        Booking booking = findById(bookingId);
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own bookings");
        }
        return updateInternal(booking, user, request.getFacilityId(), request.getGroupId(),
                request.getStartTime(), request.getEndTime(), request.getNotes());
    }

    public Booking adminUpdate(Long bookingId, Long userId, Long facilityId, Long groupId,
                               LocalDateTime startTime, LocalDateTime endTime, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        validateBookerUser(user);
        Booking booking = findById(bookingId);
        return updateInternal(booking, user, facilityId, groupId, startTime, endTime, notes);
    }

    private Booking updateInternal(Booking booking, User targetUser, Long facilityId, Long groupId,
                                   LocalDateTime startTime, LocalDateTime endTime, String notes) {
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new RuntimeException("End time must be after start time");
        }
        Facility facility = facilityRepository.findById(facilityId)
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        if (facility.getStatus() != Facility.Status.ACTIVE) {
            throw new RuntimeException("Facility is not available");
        }
        boolean overlap = bookingRepository.existsOverlap(facility, startTime, endTime, booking.getId());
        if (overlap) {
            throw new RuntimeException("Time slot is not available for this facility");
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        if (!groupMemberRepository.existsByGroupAndUser(group, targetUser)) {
            throw new RuntimeException("User must be member of the selected group");
        }

        booking.setUser(targetUser);
        booking.setFacility(facility);
        booking.setGroup(group);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setNotes(notes == null ? "" : notes);
        return bookingRepository.save(booking);
    }

    public void adminCancel(Long bookingId) {
        Booking booking = findById(bookingId);
        booking.setStatus(Booking.Status.CANCELLED);
        bookingRepository.save(booking);
    }

    private void validateBookerUser(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            throw new RuntimeException("Admins cannot own group bookings");
        }
    }
}
