package com.example.Navio.controller;

import com.example.Navio.model.Driver;
import com.example.Navio.service.DriverServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DriverServiceImple driverServiceImple;

    @GetMapping("/drivers")
    public String getPendingDriver(Model model) {
     model.addAttribute("pendingDrivers", driverServiceImple.getPendingDriver());
     return "admin/drivers";
    }

    @PostMapping("/approve-driver/{driverId}")
    public String approval(@PathVariable Long driverId) {
        driverServiceImple.approveDriver(driverId);
        return "redirect:/admin/drivers";
    }
}
