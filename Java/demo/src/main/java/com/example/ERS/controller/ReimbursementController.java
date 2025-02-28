package com.example.ERS.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ERS.dto.response.AllReimbursementResponse;
import com.example.ERS.entity.Reimbursement;
import com.example.ERS.service.ReimbursementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.transaction.Transactional;

import com.example.ERS.service.JwtService;

@RestController
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    @Autowired
    private JwtService jwtService;
    
    @Transactional
    @PostMapping("/reimbursement")
    public ResponseEntity createTicket(@RequestHeader(name="Authorization") String token, @RequestBody Reimbursement ticket) {
        if(!jwtService.validateToken(token)) return ResponseEntity.status(401).body("Bad token");
        
        Optional<Reimbursement> reimbursementOptional = Optional.ofNullable(reimbursementService.createReimbursement(token, ticket));

        if(reimbursementOptional.isPresent()) { 
            return ResponseEntity.status(200).body(reimbursementOptional.get());
        }
        return ResponseEntity.status(403).body(null);
    }

    @Transactional
    @PatchMapping("/reimbursement/status/{id}")
    public ResponseEntity editTicketStatus(@RequestHeader(name="Authorization") String token, @PathVariable Integer id , @RequestBody String status) throws JsonMappingException, JsonProcessingException {
        if(!jwtService.validateToken(token)) return ResponseEntity.status(401).body("Bad token");
        
        Optional<Reimbursement> reimbursementOptional = Optional.ofNullable(reimbursementService.updateReimbursementStatus(token, id, status));

        if(reimbursementOptional.isPresent()) { 
            return ResponseEntity.status(200).body(reimbursementOptional.get());
        }
        return ResponseEntity.status(403).body(null);
    }

    @Transactional
    @PatchMapping("/reimbursement/edit/{id}")
    public ResponseEntity editTicket(@RequestHeader(name="Authorization") String token, @PathVariable Integer id , @RequestBody String ticket) throws JsonMappingException, JsonProcessingException {
        if(!jwtService.validateToken(token)) return ResponseEntity.status(401).body("Bad token");
        
        Optional<Reimbursement> reimbursementOptional = Optional.ofNullable(reimbursementService.updateReimbursement(token, id, ticket));
        if(reimbursementOptional.isPresent()) { 
            return ResponseEntity.status(200).body(reimbursementOptional.get());
        }
        return ResponseEntity.status(403).body(null);
    }//find a way to pull data from edit target    

    @GetMapping("/users/reimbursements")
    public ResponseEntity getUserTickets(@RequestHeader(name="Authorization") String token) {
        if(!jwtService.validateToken(token)) return ResponseEntity.status(401).body("Bad token");
        
        List<Reimbursement> reimbursements = reimbursementService.getUserReimbursements(token);

        if (!reimbursements.isEmpty()) { 
            return ResponseEntity.status(200).body(reimbursements);
        }
        return ResponseEntity.status(403).body(null);
    }

    @GetMapping("/reimbursements/all")
    public ResponseEntity getAllTickets(@RequestHeader(name="Authorization") String token) {
        if(!jwtService.validateToken(token)) return ResponseEntity.status(401).body("Bad token");
        
        List<Reimbursement> reimbursementsOptional = reimbursementService.getAllReimbursements(token);

        if(!reimbursementsOptional.isEmpty()) { //find a better way to do this
            List<AllReimbursementResponse> responses = new ArrayList<>(reimbursementsOptional.stream().map(AllReimbursementResponse::new).collect(Collectors.toList()));
            return ResponseEntity.status(200).body(responses);
        }
        return ResponseEntity.status(403).body(null);
    }

    @GetMapping("/users/reimbursements?status=pending")//just auto group by status
    public ResponseEntity getUserPendingTickets(@RequestHeader(name="Authorization") String token) {

        int id = jwtService.getIdFromToken(token);
        List<Reimbursement> reimbursementsOptional = reimbursementService.getUserPendingReimbursements(token, id);

        if(reimbursementsOptional != null) { //find a better way to do this
            return ResponseEntity.status(200).body(reimbursementsOptional.toString());
        }
        return ResponseEntity.status(403).body(null);
    }

    @GetMapping("/reimbursements?status=pending")//take the status from uri param later
    public ResponseEntity getAllPendingTickets(@RequestHeader(name="Authorization") String token) {

        List<Reimbursement> reimbursementsOptional = reimbursementService.getAllReimbursements(token);

        if(reimbursementsOptional != null) { //find a better way to do this
            return ResponseEntity.status(200).body(reimbursementsOptional.toString());
        }
        return ResponseEntity.status(403).body(null);
    }

}
