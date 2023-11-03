package com.example.pidev.Controllers;


import com.example.pidev.Entities.Role;
import com.example.pidev.Services.RoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleController {


    RoleService roleService;

    @PostMapping("/addRole")
    public Role addRole(@RequestBody Role role) {
        Role  r = roleService.createNewRole(role);
        return r;
    }


}
