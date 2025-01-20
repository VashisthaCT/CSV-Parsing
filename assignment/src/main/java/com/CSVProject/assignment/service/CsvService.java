package com.CSVProject.assignment.service;

import com.CSVProject.assignment.model.CsvRecord;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvService {

    private static final String CSV_FILE_PATH = "data/test.csv"; // Path to the CSV file

    // Method to read the CSV file
    public List<CsvRecord> parseCsvFile() throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader(); // Expect header in CSV

        File file = new File(CSV_FILE_PATH);
        if (!file.exists()) {
            throw new RuntimeException("CSV file not found at path: " + CSV_FILE_PATH);
        }

        List<CsvRecord> records = new ArrayList<>();
        try {
            csvMapper.readerFor(CsvRecord.class).with(schema)
                    .<CsvRecord>readValues(file)
                    .forEachRemaining(record -> {
                        if (isValidRecord(record)) {
                            records.add(record);
                        } else {
                            System.err.println("Skipping invalid record: " + record);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while parsing CSV file: " + e.getMessage());
        }

        return records;
    }

    // Method to append data to the CSV file
    public void appendToCsvFile(CsvRecord newRecord) throws IOException {
        File file = new File(CSV_FILE_PATH);

        // Ensure the file exists
        if (!file.exists()) {
            throw new RuntimeException("CSV file not found at path: " + CSV_FILE_PATH);
        }

        // Validate the record before writing
        if (!isValidRecord(newRecord)) {
            throw new IllegalArgumentException("Invalid record: " + newRecord);
        }

        // Prepare the schema (without headers to avoid duplicate headers)
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.builder()
                .addColumn("name")
                .addColumn("age")
                .addColumn("email")
                .setUseHeader(false) // Do not rewrite headers when appending
                .build();

        try (FileWriter writer = new FileWriter(file, true)) { // Open in append mode
            csvMapper.writer(schema).writeValue(writer, newRecord);
        }
    }

    // Utility method to validate a record
    private boolean isValidRecord(CsvRecord record) {
        return record.getName() != null && !record.getName().isEmpty()
                && record.getEmail() != null && !record.getEmail().isEmpty()
                && record.getAge() > 0;
    }
}