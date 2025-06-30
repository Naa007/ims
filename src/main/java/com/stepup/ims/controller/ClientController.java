package com.stepup.ims.controller;

import com.stepup.ims.model.Client;
import com.stepup.ims.service.ClientService;
import com.stepup.ims.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.stepup.ims.constants.UIRoutingConstants.REDIRECT_TO_CLIENT_MANAGEMENT;
import static com.stepup.ims.constants.UIRoutingConstants.RETURN_TO_CLIENT_MANAGEMENT;


@Controller
@RequestMapping("/client")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @Autowired
    private ClientService clientService;
    @Autowired
    private EmployeeService employeeService;

    /**
     * Display the client management page.
     */
    @GetMapping("/list")
    public String listClients(Model model) {
        logger.info("Accessing client management list page.");
        logger.debug("Fetching all clients and coordinate employees.");
        List<Client> clients = clientService.getAllClients();

        model.addAttribute("clients", clients);
        model.addAttribute("client", new Client()); // For the add client form
        model.addAttribute("employees", employeeService.getAllCoordinateEmployees());// For the country dropdown

        logger.info("Client management page data prepared successfully.");
        return RETURN_TO_CLIENT_MANAGEMENT; // Thymeleaf template name
    }

    /**
     * Handle the request to save a new client.
     */
    @PostMapping("/save")
    public String saveClient(Client client) {
        logger.info("Saving new or updated client.");
        logger.debug("Invoking clientService.saveClient(...)");
        clientService.saveClient(client);
        logger.info("Client saved successfully. Redirecting to client management.");
        return REDIRECT_TO_CLIENT_MANAGEMENT; // Redirect to the client management page
    }

    /**
     * Handle the request to get a client by ID for editing.
     */
    @GetMapping("/edit/{id}")
    public String editClient(@PathVariable Long id, Model model) {
        logger.info("Editing client with ID: {}", id);
        logger.debug("Fetching client and coordinate employees for edit view.");

        Client client = clientService.getClientById(id).orElse(null);
        model.addAttribute("client", client);
        model.addAttribute("employeeList", employeeService.getAllCoordinateEmployees());

        logger.info("Client edit view prepared successfully for ID: {}", id);
        return "client-edit"; // Return the name of the HTML template for editing
    }
}