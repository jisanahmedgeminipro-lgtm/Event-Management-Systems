package com.shrabon.eventmanagement.config;

import com.shrabon.eventmanagement.model.*;
import com.shrabon.eventmanagement.model.enums.*;
import com.shrabon.eventmanagement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds the database on first run: a default ADMIN account, the full list of
 * event types, demo packages, customization items, vendors, gallery images and
 * editable website content. Idempotent — only runs when the users table is empty.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final StaffRepository staffRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventPackageRepository packageRepository;
    private final CustomizationItemRepository itemRepository;
    private final VendorRepository vendorRepository;
    private final GalleryItemRepository galleryRepository;
    private final WebsiteContentRepository contentRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.name}")
    private String adminName;

    public DataInitializer(UserRepository userRepository, ClientRepository clientRepository,
                           StaffRepository staffRepository, EventCategoryRepository categoryRepository,
                           EventPackageRepository packageRepository, CustomizationItemRepository itemRepository,
                           VendorRepository vendorRepository, GalleryItemRepository galleryRepository,
                           WebsiteContentRepository contentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.staffRepository = staffRepository;
        this.categoryRepository = categoryRepository;
        this.packageRepository = packageRepository;
        this.itemRepository = itemRepository;
        this.vendorRepository = vendorRepository;
        this.galleryRepository = galleryRepository;
        this.contentRepository = contentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.count() > 0) {
            log.info("Data already present — skipping seed.");
            return;
        }
        log.info("Seeding initial data for Shrabon Event Management...");

        // ---- Admin ----
        User admin = new User();
        admin.setFullName(adminName);
        admin.setEmail(adminEmail);
        admin.setPhone("+8801700000000");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        userRepository.save(admin);

        // ---- Demo staff ----
        createStaff("Shakil Ahmed", "shakil@shrabonevents.com", "Decoration Lead",
                new BigDecimal("35000"), "Decoration, Stage design, Theme planning");
        createStaff("Rumana Akter", "rumana@shrabonevents.com", "Event Coordinator",
                new BigDecimal("32000"), "Coordination, Guest management, Scheduling");

        // ---- Demo client ----
        User clientUser = new User();
        clientUser.setFullName("Demo Client");
        clientUser.setEmail("client@shrabonevents.com");
        clientUser.setPhone("+8801800000000");
        clientUser.setPassword(passwordEncoder.encode("Client@12345"));
        clientUser.setRole(Role.CLIENT);
        clientUser.setEnabled(true);
        userRepository.save(clientUser);
        Client client = new Client();
        client.setUser(clientUser);
        client.setCity("Dhaka");
        client.setAddress("Mirpur, Dhaka");
        clientRepository.save(client);

        // ---- Event categories (all required event types) ----
        String[] categories = {
                "Wedding", "Reception", "Gaye Holud", "Mehendi Night", "Birthday Party",
                "Anniversary", "Engagement", "Baby Shower", "Corporate Event", "Office Party",
                "Seminar", "Conference", "Cultural Program", "Festival Event", "Concert",
                "School Program", "College Program", "University Program", "Custom Event"
        };
        for (String name : categories) {
            EventCategory c = new EventCategory();
            c.setName(name);
            c.setDescription(name + " planning, decoration and full event management.");
            c.setActive(true);
            categoryRepository.save(c);
        }

        EventCategory wedding = categoryRepository.findByName("Wedding").orElse(null);
        EventCategory corporate = categoryRepository.findByName("Corporate Event").orElse(null);
        EventCategory birthday = categoryRepository.findByName("Birthday Party").orElse(null);

        // ---- Packages ----
        savePackage("Royal Wedding Package", wedding,
                "A grand, all-inclusive wedding experience with premium stage, lighting and catering.",
                new BigDecimal("250000"), new BigDecimal("10"), 500, true,
                true, true, true, true, true, true, true,
                List.of("Premium bridal stage", "Fresh flower decoration", "LED lighting", "Full catering", "Cinematic videography"),
                "https://picsum.photos/seed/wedpkg/600/400");
        savePackage("Classic Wedding Package", wedding,
                "Beautiful, budget-friendly wedding decoration and management.",
                new BigDecimal("120000"), new BigDecimal("5"), 300, true,
                true, true, true, false, true, true, true,
                List.of("Stage decoration", "Catering", "Photography", "Sound system"),
                "https://picsum.photos/seed/wedpkg2/600/400");
        savePackage("Corporate Conference Package", corporate,
                "Professional setup for seminars, conferences and corporate events.",
                new BigDecimal("90000"), new BigDecimal("0"), 200, true,
                false, true, true, true, true, true, true,
                List.of("Stage & podium", "Projector & screen", "Sound system", "Professional lighting"),
                "https://picsum.photos/seed/corppkg/600/400");
        savePackage("Birthday Bash Package", birthday,
                "Fun and colourful birthday decoration with catering and photography.",
                new BigDecimal("35000"), new BigDecimal("8"), 80, true,
                true, true, true, false, true, true, false,
                List.of("Themed decoration", "Balloon arch", "Catering", "Photography"),
                "https://picsum.photos/seed/bdaypkg/600/400");

        // ---- Customization items ----
        saveItem("Premium Stage Decoration", CustomizationType.DECORATION, new BigDecimal("40000"), "package");
        saveItem("Standard Stage Decoration", CustomizationType.DECORATION, new BigDecimal("20000"), "package");
        saveItem("Fresh Flower Package", CustomizationType.DECORATION, new BigDecimal("15000"), "package");
        saveItem("Full Course Dinner / plate", CustomizationType.FOOD, new BigDecimal("800"), "plate");
        saveItem("Snacks & Beverages / plate", CustomizationType.FOOD, new BigDecimal("300"), "plate");
        saveItem("Professional Photography", CustomizationType.PHOTOGRAPHY, new BigDecimal("25000"), "package");
        saveItem("Cinematic Videography", CustomizationType.VIDEOGRAPHY, new BigDecimal("30000"), "package");
        saveItem("Drone Coverage", CustomizationType.VIDEOGRAPHY, new BigDecimal("18000"), "package");
        saveItem("Decorative LED Lighting", CustomizationType.LIGHTING, new BigDecimal("12000"), "package");
        saveItem("Premium Sound System", CustomizationType.SOUND, new BigDecimal("15000"), "package");
        saveItem("Grand Stage Setup", CustomizationType.STAGE, new BigDecimal("35000"), "package");

        // ---- Vendors ----
        saveVendor("Dhaka Decorators", VendorCategory.DECORATOR, "Karim", "+8801711111111");
        saveVendor("Tasty Caterers", VendorCategory.CATERER, "Jamal", "+8801722222222");
        saveVendor("Lens & Light Studio", VendorCategory.PHOTOGRAPHER, "Sadia", "+8801733333333");
        saveVendor("Echo Sound Systems", VendorCategory.SOUND_PROVIDER, "Rafiq", "+8801744444444");

        // ---- Gallery ----
        for (int i = 1; i <= 8; i++) {
            GalleryItem g = new GalleryItem();
            g.setTitle("Event Highlight " + i);
            g.setCategory(i % 2 == 0 ? "Wedding" : "Corporate");
            g.setImageUrl("https://picsum.photos/seed/gallery" + i + "/600/400");
            galleryRepository.save(g);
        }

        // ---- Website content defaults ----
        saveContent("hero_title", "We Turn Your Moments Into Unforgettable Celebrations");
        saveContent("hero_subtitle", "Weddings, corporate events, birthdays and cultural programs — beautifully decorated and flawlessly managed.");
        saveContent("about_history", "Founded by Md. Tara Mia, Shrabon has grown into a trusted full-service event management company in Bangladesh.");
        saveContent("mission", "To craft memorable, stress-free events through creative design and reliable execution.");
        saveContent("vision", "To be the leading event management brand in Bangladesh.");
        saveContent("contact_phone", "+880 1700-000000");
        saveContent("contact_email", "info@shrabonevents.com");
        saveContent("contact_address", "Dhaka, Bangladesh");

        log.info("Seed complete. Admin login: {} / {}", adminEmail, adminPassword);
    }

    private void createStaff(String name, String email, String position, BigDecimal salary, String skills) {
        User u = new User();
        u.setFullName(name);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode("Staff@12345"));
        u.setRole(Role.STAFF);
        u.setEnabled(true);
        userRepository.save(u);
        Staff s = new Staff();
        s.setUser(u);
        s.setPosition(position);
        s.setSalary(salary);
        s.setSkills(skills);
        s.setAvailability(StaffAvailability.AVAILABLE);
        s.setJoinDate(LocalDate.now());
        staffRepository.save(s);
    }

    private void savePackage(String name, EventCategory category, String desc, BigDecimal base, BigDecimal discount,
                             int capacity, boolean featured, boolean decoration, boolean catering, boolean photography,
                             boolean videography, boolean lighting, boolean sound, boolean stage,
                             List<String> features, String image) {
        EventPackage p = new EventPackage();
        p.setName(name);
        p.setCategory(category);
        p.setDescription(desc);
        p.setBasePrice(base);
        p.setDiscountPercent(discount);
        p.setGuestCapacity(capacity);
        p.setFeatured(featured);
        p.setActive(true);
        p.setDecoration(decoration);
        p.setCatering(catering);
        p.setPhotography(photography);
        p.setVideography(videography);
        p.setLighting(lighting);
        p.setSoundSystem(sound);
        p.setStageSetup(stage);
        p.getFeatures().addAll(features);
        p.addImage(new PackageImage(image));
        packageRepository.save(p);
    }

    private void saveItem(String name, CustomizationType type, BigDecimal price, String unit) {
        CustomizationItem item = new CustomizationItem();
        item.setName(name);
        item.setType(type);
        item.setPrice(price);
        item.setUnit(unit);
        item.setActive(true);
        item.setDescription(name);
        itemRepository.save(item);
    }

    private void saveVendor(String name, VendorCategory category, String contact, String phone) {
        Vendor v = new Vendor();
        v.setName(name);
        v.setCategory(category);
        v.setContactPerson(contact);
        v.setPhone(phone);
        v.setActive(true);
        vendorRepository.save(v);
    }

    private void saveContent(String key, String value) {
        contentRepository.save(new WebsiteContent(key, value));
    }
}
