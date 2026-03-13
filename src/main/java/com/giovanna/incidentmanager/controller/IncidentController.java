package com.giovanna.incidentmanager.controller;


import com.giovanna.incidentmanager.model.Incident;
import com.giovanna.incidentmanager.repository.IncidentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/incidents")
public class IncidentController {

    private final IncidentRepository incidentRepository;

    public IncidentController(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    @GetMapping
    public String listIncidents(@RequestParam(defaultValue = "0") int page, Model model) {

        if (incidentRepository.count() == 0) {
            Incident i1 = new Incident(null,
                    "Error al guardar usuario",
                    "Se produce un error al guardar un usuario en el formulario.",
                    "HIGH",
                    "OPEN",
                    "Giovanna",
                    LocalDate.now());

            Incident i2 = new Incident(null,
                    "Fallo en exportación Excel",
                    "La exportación genera archivo vacío.",
                    "MEDIUM",
                    "IN_PROGRESS",
                    "Vanessa",
                    LocalDate.now());

            Incident i3 = new Incident(null,
                    "Validación incorrecta en login",
                    "El formulario permite campos vacíos.",
                    "LOW",
                    "CLOSED",
                    "Admin",
                    LocalDate.now());

            incidentRepository.save(i1);
            incidentRepository.save(i2);
            incidentRepository.save(i3);
        }

        Pageable pageable = PageRequest.of(page, 5);
        Page<Incident> incidentPage = incidentRepository.findAll(pageable);

        long total = incidentRepository.count();
        long openCount = incidentRepository.findAll().stream().filter(i -> "OPEN".equals(i.getStatus())).count();
        long inProgressCount = incidentRepository.findAll().stream().filter(i -> "IN_PROGRESS".equals(i.getStatus())).count();
        long closedCount = incidentRepository.findAll().stream().filter(i -> "CLOSED".equals(i.getStatus())).count();

        model.addAttribute("incidents", incidentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", incidentPage.getTotalPages());
        model.addAttribute("totalItems", incidentPage.getTotalElements());

        model.addAttribute("total", total);
        model.addAttribute("openCount", openCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("closedCount", closedCount);

        return "incidents";
    }

    @GetMapping("/{id}")
    public String viewIncident(@PathVariable Long id, Model model) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada: " + id));

        model.addAttribute("incident", incident);
        return "incident-detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("incident", new Incident());
        model.addAttribute("formTitle", "Nueva incidencia");
        return "incident-form";
    }

    @PostMapping("/save")
    public String saveIncident(
            @Valid @ModelAttribute Incident incident,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("formTitle", "Formulario con errores");
            return "incident-form";
        }

        if (incident.getCreatedDate() == null) {
            incident.setCreatedDate(LocalDate.now());
        }

        incidentRepository.save(incident);
        return "redirect:/incidents";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Incidencia no encontrada: " + id));

        model.addAttribute("incident", incident);
        model.addAttribute("formTitle", "Editar incidencia");
        return "incident-form";
    }

    @GetMapping("/delete/{id}")
    public String deleteIncident(@PathVariable Long id) {
        incidentRepository.deleteById(id);
        return "redirect:/incidents";
    }
}