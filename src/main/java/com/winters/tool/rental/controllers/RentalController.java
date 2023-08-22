package com.winters.tool.rental.controllers;

import com.winters.tool.rental.data.RentalAgreement;
import com.winters.tool.rental.data.Tool;
import com.winters.tool.rental.data.Tool.Brand;
import com.winters.tool.rental.data.Tool.Type;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/rental")
public class RentalController {


    @GetMapping
    public RentalAgreement getTestRentalAgreement() {
        Tool tool = Tool.builder()
                .brand(Brand.DEWALT)
                .type(Type.CHAINSAW)
                .build();
        RentalAgreement ag = RentalAgreement.builder()
                .tool(tool)
                .build();
        System.out.println(ag.toString());
        return ag;
    }

}
