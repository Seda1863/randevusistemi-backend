package com.randevu.randevusistemibackend.service;

import com.randevu.randevusistemibackend.model.Appointment;
import com.randevu.randevusistemibackend.model.Provider;
import com.randevu.randevusistemibackend.model.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.sender-name}")
    private String senderName;

    /**
     * Asynchronously send email notification to provider about new appointment
     */
    @Async
    public void sendNewAppointmentNotificationToProvider(Appointment appointment) {
        try {
            Provider provider = appointment.getProvider();
            User user = appointment.getUser();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(provider.getEmail());
            helper.setSubject("Yeni Randevu Bildirimi");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("providerName", provider.getFullName());
            context.setVariable("userName", user.getFullName());
            context.setVariable("serviceName", appointment.getServiceName());
            context.setVariable("appointmentDate", appointment.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            context.setVariable("appointmentTime", appointment.getStartTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            context.setVariable("duration", java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes());
            context.setVariable("notes", appointment.getNotes() != null ? appointment.getNotes() : "");

            // Process template
            String htmlContent = templateEngine.process("new-appointment-provider-notification.html", context);
            helper.setText(htmlContent, true);

            // Add ICS attachment
            byte[] icsBytes = generateIcsForAppointment(appointment);
            helper.addAttachment("randevu.ics", new ByteArrayDataSource(icsBytes, "text/calendar"));

            mailSender.send(message);
            log.info("New appointment notification email sent to provider {}", provider.getEmail());
        } catch (Exception e) {
            log.error("Error sending new appointment notification to provider: {}", e.getMessage(), e);
        }
    }

    /**
     * Asynchronously send email notification to user about appointment confirmation
     */
    @Async
    public void sendAppointmentConfirmationToUser(Appointment appointment) {
        try {
            User user = appointment.getUser();
            Provider provider = appointment.getProvider();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(user.getEmail());
            helper.setSubject("Randevu Talebiniz Alındı"); // Changed subject

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("userName", user.getFullName());
            context.setVariable("providerBusinessName", provider.getBusinessName());
            context.setVariable("serviceName", appointment.getServiceName());
            context.setVariable("appointmentDate", appointment.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            context.setVariable("appointmentTime", appointment.getStartTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            context.setVariable("duration", java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes());
            context.setVariable("notes", appointment.getNotes() != null ? appointment.getNotes() : "");
            
            // Process template
            String htmlContent = templateEngine.process("appointment-request-user.html", context); // Changed template
            helper.setText(htmlContent, true);

            // Add ICS attachment
            byte[] icsBytes = generateIcsForAppointment(appointment);
            helper.addAttachment("randevu.ics", new ByteArrayDataSource(icsBytes, "text/calendar"));

            mailSender.send(message);
            log.info("Appointment request email sent to user {}", user.getEmail()); // Updated log message
        } catch (Exception e) {
            log.error("Error sending appointment request email to user: {}", e.getMessage(), e); // Updated log message
        }
    }

    @Async
    public void sendAppointmentConfirmedToUser(Appointment appointment) {
        try {
            User user = appointment.getUser();
            Provider provider = appointment.getProvider();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(user.getEmail());
            helper.setSubject("Randevu Onayınız");

            // Prepare Thymeleaf context
            Context context = new Context();
            context.setVariable("userName", user.getFullName());
            context.setVariable("providerBusinessName", provider.getBusinessName());
            context.setVariable("serviceName", appointment.getServiceName());
            context.setVariable("appointmentDate", appointment.getStartTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            context.setVariable("appointmentTime", appointment.getStartTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            context.setVariable("duration", java.time.Duration.between(appointment.getStartTime(), appointment.getEndTime()).toMinutes());
            context.setVariable("notes", appointment.getNotes() != null ? appointment.getNotes() : "");

            // Process template
            String htmlContent = templateEngine.process("appointment-confirmation-user.html", context);
            helper.setText(htmlContent, true);


            // Add ICS attachment
            byte[] icsBytes = generateIcsForAppointment(appointment);
            helper.addAttachment("randevu.ics", new ByteArrayDataSource(icsBytes, "text/calendar"));

            mailSender.send(message);
            log.info("Appointment confirmation email sent to user {}", user.getEmail());
        } catch (Exception e) {
            log.error("Error sending appointment confirmation to user: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate ICS calendar file for the appointment
     */
    private byte[] generateIcsForAppointment(Appointment appointment) throws Exception {
        // Create a calendar instance
        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Randevu Sistemi//iCal4j 3.0//TR"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        // Get timezone
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("Europe/Istanbul");

        // Create the event
        DateTime start = new DateTime(Date.from(appointment.getStartTime().atZone(ZoneId.of("Europe/Istanbul")).toInstant()));
        start.setTimeZone(timezone);

        DateTime end = new DateTime(Date.from(appointment.getEndTime().atZone(ZoneId.of("Europe/Istanbul")).toInstant()));
        end.setTimeZone(timezone);

        // Create a unique identifier for this event
        UidGenerator uidGenerator = new RandomUidGenerator();
        Uid uid = uidGenerator.generateUid();

        // Prepare event title and description
        String summary = String.format("Randevu: %s - %s",
                appointment.getProvider().getBusinessName(),
                appointment.getServiceName() != null ? appointment.getServiceName() : "Randevu");

        StringBuilder descBuilder = new StringBuilder();
        descBuilder.append("Sağlayıcı: ").append(appointment.getProvider().getFullName()).append("\\n");
        descBuilder.append("Hizmet: ").append(appointment.getServiceName() != null ?
                appointment.getServiceName() : "Belirtilmemiş").append("\\n");

        if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
            descBuilder.append("Notlar: ").append(appointment.getNotes()).append("\\n");
        }

        // Create location info if available
        String locationInfo = null;
        if (appointment.getProvider().getAddress() != null) {
            locationInfo = appointment.getProvider().getAddress().toString(); // Assuming Address class has a meaningful toString()
        }

        // Create the event with the information
        VEvent event = new VEvent(start, end, summary);
        event.getProperties().add(uid);
        event.getProperties().add(new Description(descBuilder.toString()));

        if (locationInfo != null) {
            event.getProperties().add(new Location(locationInfo));
        }

        // Add organizer
        event.getProperties().add(new Organizer("mailto:" + appointment.getProvider().getEmail()));

        calendar.getComponents().add(event);

        // Write to output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, baos);

        return baos.toByteArray();
    }
}
