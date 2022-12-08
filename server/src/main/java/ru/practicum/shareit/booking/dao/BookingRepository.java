package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> getBookingsByBookerOrderByStartDesc(User booker, Pageable pageRequest);

    Page<Booking> getBookingsByBookerAndStatusOrderByStartDesc(User booker, BookingStatus status, Pageable pageRequest);

    Optional<Booking> getBookingByItemAndEndBeforeOrderByEndDesc(Item item, LocalDateTime today);

    Optional<Booking> getBookingByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime today);

    Optional<Booking> getBookingByBookerAndItemAndStatusEqualsAndEndBefore(User booker, Item item, BookingStatus status, LocalDateTime today);

    Page<Booking> getBookingsByBookerAndEndBeforeOrderByStartDesc(User booker, LocalDateTime today, Pageable pageRequest);

    Page<Booking> getBookingsByBookerAndStartAfterOrderByStartDesc(User booker, LocalDateTime today, Pageable pageRequest);

    @Query("SELECT b " +
            "FROM bookings b " +
            "WHERE b.booker = :booker " +
            "AND :today BETWEEN  b.start AND b.end " +
            "ORDER BY b.start DESC")
    Page<Booking> getBookingsCurrent(User booker, LocalDateTime today, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings " +
            "INNER JOIN items " +
            "ON items.id = bookings.item_id " +
            "WHERE items.owner_id = ?1 " +
            "GROUP BY bookings.id, items.id " +
            "ORDER BY bookings.start_booking DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM bookings " +
                    "INNER JOIN items " +
                    "ON items.id = bookings.item_id " +
                    "WHERE items.owner_id = ?1 " +
                    "GROUP BY items.id", nativeQuery = true)
    Page<Booking> getAllBookingsByOwner(Long ownerId, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings b " +
            "INNER JOIN items i " +
            "ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.status = CAST (?2 AS booking_status) " +
            "ORDER BY b.start_booking DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM bookings b " +
                    "INNER JOIN items i " +
                    "ON i.id = b.item_id " +
                    "WHERE i.owner_id = ?1 " +
                    "AND b.status = ?2",
            nativeQuery = true)
    Page<Booking> getAllBookingsByOwnerAndStatus(Long ownerId, String status, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings b " +
            "INNER JOIN items i " +
            "ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND ?2 BETWEEN b.start_booking AND b.end_of_booking " +
            "ORDER BY b.start_booking DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM bookings b " +
                    "INNER JOIN items i " +
                    "ON i.id = b.item_id " +
                    "WHERE i.owner_id = ?1 " +
                    "AND ?2 BETWEEN b.start_booking AND b.end_of_booking ",
            nativeQuery = true)
    Page<Booking> getAllBookingsByOwnerCurrent(Long ownerId, LocalDateTime today, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings b " +
            "INNER JOIN items i " +
            "ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.start_booking >= ?2 " +
            "ORDER BY b.start_booking DESC",
            countQuery = "SELECT COUNT(*) " +
                    "FROM bookings b " +
                    "INNER JOIN items i " +
                    "ON i.id = b.item_id " +
                    "WHERE i.owner_id = ?1 " +
                    "AND b.start_booking >= ?2 ",
            nativeQuery = true)
    Page<Booking> getAllBookingsByOwnerInFuture(Long ownerId, LocalDateTime today, Pageable pageRequest);

    @Query(value = "SELECT * " +
            "FROM bookings b " +
            "INNER JOIN items i " +
            "ON i.id = b.item_id " +
            "WHERE i.owner_id = ?1 " +
            "AND b.end_of_booking <= ?2 " +
            "ORDER BY b.start_booking DESC",
            countQuery = "SELECT count(*) " +
                    "FROM bookings b " +
                    "INNER JOIN items i " +
                    "ON i.id = b.item_id " +
                    "WHERE i.owner_id = ?1 " +
                    "AND b.end_of_booking <= ?2 ",
            nativeQuery = true)
    Page<Booking> getAllBookingsByOwnerInPast(Long ownerId, LocalDateTime today, Pageable pageRequest);
}
