package com.apextransport.service;

import com.apextransport.entity.AuditLog;
import com.apextransport.entity.User;
import com.apextransport.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Transactional
    public AuditLog log(User user, String action, String category, String details) {
        try {
            AuditLog auditLog = new AuditLog(user, action, category, details, extractClientIp());
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("Error saving audit log: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public AuditLog log(User user, String role, String action, String category, String details) {
        try {
            AuditLog auditLog = new AuditLog(user, action, category, details, extractClientIp());
            if (role != null && !role.isBlank()) {
                auditLog.setRole(role);
            }
            return auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("Error saving audit log: " + e.getMessage());
            return null;
        }
    }

    private String extractClientIp() {
        try {
            org.springframework.web.context.request.ServletRequestAttributes attrs = (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder
                    .getRequestAttributes();
            if (attrs == null)
                return "unknown";
            jakarta.servlet.http.HttpServletRequest req = attrs.getRequest();
            String forwarded = req.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return req.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }

    public List<AuditLog> getUserLogs(User user) {
        return auditLogRepository.findTop50ByUserOrderByCreatedAtDesc(user);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findTop100ByOrderByCreatedAtDesc();
    }
}
