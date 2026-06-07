package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.model.Booking;
import com.shrabon.eventmanagement.model.ContactMessage;
import com.shrabon.eventmanagement.model.Staff;
import com.shrabon.eventmanagement.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean enabled;

    @Value("${app.mail.from:no-reply@shrabonevents.com}")
    private String from;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Async
    public void bookingStatus(Booking b) {

        if (b == null || b.getClient() == null || b.getClient().getUser() == null) {
            return;
        }

        String name = b.getClient().getUser().getFullName();

        String subject =
                "Booking " + b.getBookingReference()
                        + " — "
                        + b.getStatus().getLabel();

        String body =
                "Dear " + name + ",\n\n"
                        + "Your booking status has been updated.\n\n"
                        + "Reference : " + b.getBookingReference() + "\n"
                        + "Status    : " + b.getStatus().getLabel() + "\n"
                        + "Event     : "
                        + (b.getCategory() != null
                        ? b.getCategory().getName()
                        : "Event")
                        + "\n"
                        + "Date      : " + b.getEventDate() + "\n"
                        + "Venue     : " + b.getVenue() + "\n"
                        + "Total     : BDT " + b.getTotalAmount() + "\n\n"
                        + "Thank you for choosing Shrabon Decorator & Event Management.\n";

        send(
                b.getClient().getUser().getEmail(),
                subject,
                body
        );
    }

    @Async
    public void contactAcknowledgement(ContactMessage msg) {

        if (msg == null) {
            return;
        }

        String subject =
                "We received your message — Shrabon Events";

        String body =
                "Dear " + msg.getName() + ",\n\n"
                        + "Thank you for contacting Shrabon Decorator & Event Management. "
                        + "We have received your message and our team will get back to you shortly.\n\n"
                        + "Your message:\n"
                        + "\"" + msg.getMessage() + "\"\n\n"
                        + "Warm regards,\n"
                        + "Shrabon Events Team";

        send(
                msg.getEmail(),
                subject,
                body
        );
    }

    @Async
    public void staffAssignment(Collection<Staff> staffList, Booking b) {

        if (staffList == null || b == null) {
            return;
        }

        for (Staff s : staffList) {

            if (s == null || s.getUser() == null) {
                continue;
            }

            String subject =
                    "New event assigned — "
                            + b.getBookingReference();

            String body =
                    "Dear " + s.getUser().getFullName() + ",\n\n"
                            + "You have been assigned to an upcoming event.\n\n"
                            + "Reference : " + b.getBookingReference() + "\n"
                            + "Event     : "
                            + (b.getCategory() != null
                            ? b.getCategory().getName()
                            : "Event")
                            + "\n"
                            + "Date      : " + b.getEventDate() + "\n"
                            + "Venue     : " + b.getVenue() + "\n\n"
                            + "Please check your Staff dashboard for full details.\n";

            send(
                    s.getUser().getEmail(),
                    subject,
                    body
            );
        }
    }

    @Async
    public void taskAssignment(Task t) {

        if (t == null
                || t.getAssignedStaff() == null
                || t.getAssignedStaff().getUser() == null) {
            return;
        }

        var user = t.getAssignedStaff().getUser();

        String subject =
                "New task assigned — "
                        + t.getTitle();

        String body =
                "Dear " + user.getFullName() + ",\n\n"
                        + "A new task has been assigned to you.\n\n"
                        + "Title    : " + t.getTitle() + "\n"
                        + "Priority : " + t.getPriority().getLabel() + "\n"
                        + "Deadline : "
                        + (t.getDeadline() != null
                        ? t.getDeadline()
                        : "Not set")
                        + "\n"
                        + "Status   : " + t.getStatus().getLabel() + "\n\n"
                        + (t.getDescription() != null
                        ? "Details: " + t.getDescription() + "\n\n"
                        : "")
                        + "Please check your Staff dashboard.\n";

        send(
                user.getEmail(),
                subject,
                body
        );
    }

    private void send(String to, String subject, String body) {

        if (!enabled
                || mailSender == null
                || to == null
                || to.isBlank()) {

            log.debug(
                    "Email skipped (enabled={}, hasSender={}, to={})",
                    enabled,
                    mailSender != null,
                    to
            );

            return;
        }

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info(
                    "Notification email sent to {}",
                    to
            );

        } catch (Exception ex) {

            log.warn(
                    "Could not send email to {}: {}",
                    to,
                    ex.getMessage()
            );
        }
    }
}