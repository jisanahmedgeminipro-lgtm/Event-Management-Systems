package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.BookingForm;
import com.shrabon.eventmanagement.dto.QuoteResult;
import com.shrabon.eventmanagement.exception.BookingConflictException;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.*;
import com.shrabon.eventmanagement.model.enums.BookingStatus;
import com.shrabon.eventmanagement.model.enums.PaymentStatus;
import com.shrabon.eventmanagement.repository.*;
import com.shrabon.eventmanagement.service.BookingService;
import com.shrabon.eventmanagement.service.CustomizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ClientRepository clientRepository;
    private final EventPackageRepository packageRepository;
    private final EventCategoryRepository categoryRepository;
    private final CustomizationItemRepository itemRepository;
    private final StaffRepository staffRepository;
    private final CustomizationService customizationService;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ClientRepository clientRepository,
                              EventPackageRepository packageRepository,
                              EventCategoryRepository categoryRepository,
                              CustomizationItemRepository itemRepository,
                              StaffRepository staffRepository,
                              CustomizationService customizationService) {
        this.bookingRepository = bookingRepository;
        this.clientRepository = clientRepository;
        this.packageRepository = packageRepository;
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.staffRepository = staffRepository;
        this.customizationService = customizationService;
    }

    @Override
    @Transactional
    public Booking createBooking(Long clientUserId, BookingForm form) {
        Client client = clientRepository.findByUserId(clientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found."));

        // ---- Double booking prevention (same venue + same date) ----
        if (bookingRepository.existsVenueConflict(form.getVenue(), form.getEventDate(), null)) {
            throw new BookingConflictException(
                    "The venue \"" + form.getVenue() + "\" is already booked on "
                            + form.getEventDate() + ". Please choose another date or venue.");
        }

        Booking booking = new Booking();
        booking.setBookingReference(generateReference());
        booking.setClient(client);
        booking.setEventDate(form.getEventDate());
        booking.setEventTime(form.getEventTime());
        booking.setVenue(form.getVenue().trim());
        booking.setGuestCount(form.getGuestCount());
        booking.setSpecialRequirements(form.getSpecialRequirements());
        booking.setStatus(BookingStatus.PENDING);

        if (form.getCategoryId() != null) {
            EventCategory category = categoryRepository.findById(form.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Event type not found."));
            booking.setCategory(category);
        }

        EventPackage pkg = null;
        if (form.getPackageId() != null) {
            pkg = packageRepository.findById(form.getPackageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Package not found."));
            booking.setEventPackage(pkg);
        }

        // ---- Automatic pricing (base + add-ons - discount) ----
        QuoteResult quote = customizationService.calculateQuote(form.getPackageId(), form.getCustomizationItemIds());
        booking.setBaseAmount(quote.getBasePrice());
        booking.setAdditionalAmount(quote.getAdditionalCost());
        booking.setDiscountAmount(quote.getDiscount());
        booking.setTotalAmount(quote.getFinalPrice());

        // ---- Customization line items ----
        if (form.getCustomizationItemIds() != null) {
            List<CustomizationItem> items = itemRepository.findAllById(form.getCustomizationItemIds());
            for (CustomizationItem item : items) {
                BookingCustomization bc = new BookingCustomization();
                bc.setItem(item);
                bc.setQuantity(1);
                bc.setLineTotal(item.getPrice());
                booking.addCustomization(bc);
            }
        }

        // ---- Aggregate payment record ----
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setTotalAmount(booking.getTotalAmount());
        payment.setPaidAmount(java.math.BigDecimal.ZERO);
        payment.recalculate();
        payment.setStatus(PaymentStatus.PENDING);
        booking.setPayment(payment);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    @Override
    public Booking getByReference(String reference) {
        return bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + reference));
    }

    @Override
    public List<Booking> findAll() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<Booking> findByClientUserId(Long clientUserId) {
        Client client = clientRepository.findByUserId(clientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found."));
        return bookingRepository.findByClientIdOrderByCreatedAtDesc(client.getId());
    }

    @Override
    public List<Booking> findByStaffUserId(Long staffUserId) {
        Staff staff = staffRepository.findByUserId(staffUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found."));
        return bookingRepository.findByAssignedStaffId(staff.getId());
    }

    @Override
    public List<Booking> findUpcoming() {
        return bookingRepository.findUpcoming(LocalDate.now());
    }

    @Override
    @Transactional
    public Booking updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = getById(bookingId);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking assignStaff(Long bookingId, Set<Long> staffIds) {
        Booking booking = getById(bookingId);
        Set<Staff> staffSet = new HashSet<>();
        if (staffIds != null) {
            staffSet.addAll(staffRepository.findAllById(staffIds));
        }
        booking.setAssignedStaff(staffSet);
        return bookingRepository.save(booking);
    }

    @Override
    public boolean isVenueAvailable(String venue, LocalDate date, Long excludeBookingId) {
        return !bookingRepository.existsVenueConflict(venue, date, excludeBookingId);
    }

    @Override
    public long countByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }

    /** Generates a unique, human-friendly booking reference, e.g. SHB-2026-004821. */
    private String generateReference() {
        String ref;
        do {
            int rand = ThreadLocalRandom.current().nextInt(100000, 999999);
            ref = "SHB-" + Year.now().getValue() + "-" + rand;
        } while (bookingRepository.findByBookingReference(ref).isPresent());
        return ref;
    }
}
