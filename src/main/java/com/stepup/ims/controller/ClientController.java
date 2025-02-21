package com.stepup.ims.controller;

import com.stepup.ims.model.Client;
import com.stepup.ims.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

     @GetMapping("/list")
    public String listClients(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "clientList";
    }

    @GetMapping("/form")
    public String getClientForm(Model model) {
        model.addAttribute("client", new Client());
        return "clientForm";
    }

    @PostMapping("/save")
    public String saveClient(@ModelAttribute Client client) {
        clientService.saveClient(client);
        return "redirect:/client/list";
    }

    @GetMapping("/delete/{id}")
    public String deleteClient(@PathVariable Long id) {
        clientService.deleteClientById(id);
        return "redirect:/client/list";
    }
}
