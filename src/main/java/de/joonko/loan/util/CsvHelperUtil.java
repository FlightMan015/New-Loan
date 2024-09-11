package de.joonko.loan.util;

import de.joonko.loan.exception.CsvGenerationFailedException;
import de.joonko.loan.reporting.api.model.Exportable;

import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

@UtilityClass
@Slf4j
public class CsvHelperUtil {

    public static <E extends Exportable> InputStreamResource constructCsv(final List<E> objects) {
        final var someObject = objects.stream().findAny();

        if (someObject.isPresent()) {
            final var fieldsForExport = someObject.get().getFieldsForExport();
            final List<String> csvHeader = new ArrayList<>(fieldsForExport.keySet());

            final List<List<String>> csvBody = objects.stream().map(obj -> fieldsForExport.values().stream()
                    .map(getter -> invokeGetter(obj, getter))
                    .map(val -> val.map(Object::toString).orElse(""))
                    .collect(toList())
            ).collect(toList());

            final var byteArrayOutputStream = getByteArrayOutputStream(csvHeader, csvBody);
            return new InputStreamResource(byteArrayOutputStream);
        } else {
            return new InputStreamResource(InputStream.nullInputStream());
        }
    }

    private static ByteArrayInputStream getByteArrayOutputStream(List<String> csvHeader, List<List<String>> csvBody) {
        try (
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT)) {
            csvPrinter.printRecord(csvHeader);
            for (List<String> item : csvBody) {
                csvPrinter.printRecord(item);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (final IOException e) {
            throw new CsvGenerationFailedException(String.format("Unable to convert the data to csv, error message - %s, cause - %s", e.getMessage(), e.getCause()));
        }
    }

    private static <E extends Exportable> Optional<Object> invokeGetter(final E obj, final Method getter) {
        try {
            return ofNullable(getter.invoke(obj));
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error occurred when constructing csv file for object {}, exception message - {}, cause - {}", obj.getClass(), e.getMessage(), e.getCause());
        }
        return Optional.empty();
    }
}
