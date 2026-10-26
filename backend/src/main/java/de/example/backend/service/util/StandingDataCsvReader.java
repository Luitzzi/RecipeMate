package de.example.backend.service.util;

import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Reads the standing data CSV files from resources/db.
 */
public final class StandingDataCsvReader {

    private StandingDataCsvReader() {
    }

    /**
     * @return The stripped columns of every non-blank line except the header
     * @throws IllegalArgumentException If a line does not have the expected number of columns
     */
    public static List<String[]> readRows(Resource csv, int expectedColumns) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(csv.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1) // Header
                    .filter(line -> !line.isBlank())
                    .map(line -> toColumns(line, expectedColumns))
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read CSV " + csv.getDescription(), e);
        }
    }

    private static String[] toColumns(String line, int expectedColumns) {
        String[] columns = line.split(",", -1);
        if (columns.length != expectedColumns)
            throw new IllegalArgumentException("Invalid CSV line: " + line);

        return Arrays.stream(columns)
                .map(String::strip)
                .toArray(String[]::new);
    }
}
