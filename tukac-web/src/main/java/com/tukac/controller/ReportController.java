package com.tukac.controller;

import com.tukac.model.ActivityLog;
import com.tukac.repository.ActivityLogRepository;
import com.tukac.repository.TransactionRepository;
import com.tukac.repository.UserRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('CHAIRPERSON','VICE-CHAIRPERSON','TREASURER','SECRETARY')")
public class ReportController {

    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private ActivityLogRepository activityLogRepository;

    @GetMapping("/users")
    public ResponseEntity<byte[]> generateUsersReport() {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/users_report.jrxml");
            if (reportStream == null) {
                return ResponseEntity.internalServerError().build();
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(userRepository.findAll());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "TUKAC — User Membership List");
            parameters.put("GeneratedBy", getCurrentUserDisplayName());
            parameters.put("GeneratedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
            parameters.put("TotalMembers", (long) userRepository.findAll().size());

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

            logReportActivity("REPORT_GENERATED", "Users membership PDF report generated");

            String filename = "tukac_members_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/finances")
    public ResponseEntity<byte[]> generateFinanceReport() {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/finance_report.jrxml");
            if (reportStream == null) {
                return ResponseEntity.internalServerError().build();
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    transactionRepository.findAllByOrderByTransactionDateDesc());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "TUKAC — Financial Statement");
            parameters.put("GeneratedBy", getCurrentUserDisplayName());
            parameters.put("GeneratedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            Double balance = transactionRepository.calculateBalance();
            parameters.put("NetBalance", balance != null ? balance : 0.0);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

            logReportActivity("REPORT_GENERATED", "Financial statement PDF report generated");

            String filename = "tukac_finances_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/activity-logs")
    @PreAuthorize("hasRole('CHAIRPERSON')")
    public ResponseEntity<byte[]> generateActivityLogReport() {
        try {
            InputStream reportStream = getClass().getResourceAsStream("/reports/activity_log_report.jrxml");
            if (reportStream == null) {
                return ResponseEntity.internalServerError().build();
            }
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
                    activityLogRepository.findAllByOrderByTimestampDesc());
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ReportTitle", "TUKAC — System Activity Audit Log");
            parameters.put("GeneratedBy", getCurrentUserDisplayName());
            parameters.put("GeneratedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);

            String filename = "tukac_audit_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")) + ".pdf";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String getCurrentUserDisplayName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return "System";
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .map(u -> u.getName() + " (" + u.getRole() + ")")
                .orElse(email);
    }

    private void logReportActivity(String action, String details) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return;
        userRepository.findByEmail(auth.getName()).ifPresent(user -> {
            ActivityLog log = new ActivityLog(user.getId(), user.getName(), action, details, "internal");
            activityLogRepository.save(log);
        });
    }
}
