package com.pennypulse.backend.api;

import com.pennypulse.backend.service.ReminderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reminders")
public class ReminderController {
    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @GetMapping("/status")
    public ReminderService.ReminderStatus status() {
        return reminderService.status();
    }
}
