package com.CSVProject.assignment.controller;

import com.CSVProject.assignment.model.CsvRecord;
import com.CSVProject.assignment.service.CsvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvService csvService;

    // Endpoint to read the CSV file
    @GetMapping("/read")
    public ResponseEntity<List<CsvRecord>> readCsv() {
        try {
            List<CsvRecord> records = csvService.parseCsvFile();
            return ResponseEntity.ok(records);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Endpoint to append a new record to the CSV file
    @PostMapping("/append")
    public ResponseEntity<String> appendToCsv(@RequestBody CsvRecord newRecord) {
        try {
            csvService.appendToCsvFile(newRecord);
            return ResponseEntity.ok("Record added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to append data: " + e.getMessage());
        }
    }
}