package com.stepup.ims.controller;

import com.stepup.ims.model.Client;
import com.stepup.ims.model.Employee;
import com.stepup.ims.service.ClientService;
import com.stepup.ims.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Arrays;


@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private EmployeeService employeeService;
    private static final List<String> COUNTRIES_LIST = Arrays.asList(
            "India", "USA", "UK", "Canada", "Australia", "Germany", "France", "Japan"
    );
    /**
     * Display the client management page.
     */
    @GetMapping("/list")
    public String listClients(Model model) {
        List<Client> clients = clientService.getAllClients();
        for (Client client : clients) {
            System.out.println("Client: " + client.getClientName() + ", Confirmation Date: " + client.getConfirmationDate());
        }
        model.addAttribute("clients", clients);
        model.addAttribute("client", new Client()); // For the add client form
        model.addAttribute("countriesList", COUNTRIES_LIST);
        model.addAttribute("employees", employeeService.getAllCoordinateEmployees());// For the country dropdown
        return "client-management"; // Thymeleaf template name
    }

    /**
     * Handle the request to save a new client.
     */
    @PostMapping("/save")
    public String saveClient(Client client) {
        clientService.saveClient(client);
        return "redirect:/client/list"; // Redirect to the client management page
    }

    /**
     * Handle the request to get a client by ID for editing.
     */
    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id).orElse(null);
        model.addAttribute("client", client);
        model.addAttribute("employeeList", employeeService.getAllCoordinateEmployees());
        return "client-edit"; // Return the name of the HTML template for editing
    }

    /**
     * Handle the request to update an existing client.
     */
    @PostMapping("/update")
    public String updateClient(@ModelAttribute Client client) {
        clientService.saveClient(client);
        return "redirect:/client/list"; // Redirect to the client management page
    }
    // View client details
    @GetMapping("/view/{id}")
    public String viewClient(@PathVariable("id") Long id, Model model) {
        Client client = clientService.getClientById(id).orElse(null);
        model.addAttribute("client", client);
        return "view-client"; // Thymeleaf template for viewing
    }
}