package de.joonko.loan.reporting.api.model;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public interface Exportable {

    LinkedHashMap<String, Method> getFieldsForExport();
}
