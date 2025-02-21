package com.stepup.ims.controller;

import com.stepup.ims.model.Client;
import com.stepup.ims.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    /**
     * Display the client management page.
     */
    @GetMapping("/clientList") // Updated mapping
    public String showClientManagementPage(Model model) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "client-management"; // Return the name of the HTML template
    }

    /**
     * Handle the request to save a new client.
     */
    @PostMapping("/save-client")
    public String saveClient(@ModelAttribute Client client) {
        clientService.saveClient(client);
        return "redirect:/client/clientList"; // Redirect to the client management page
    }

    /**
     * Handle the request to get a client by ID for editing.
     */
    @GetMapping("/edit-client/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id).orElse(null);
        model.addAttribute("client", client);
        return "client-edit"; // Return the name of the HTML template for editing
    }

    /**
     * Handle the request to update an existing client.
     */
    @PostMapping("/update-client")
    public String updateClient(@ModelAttribute Client client) {
        clientService.saveClient(client);
        return "redirect:/client/clientList"; // Redirect to the client management page
    }

}