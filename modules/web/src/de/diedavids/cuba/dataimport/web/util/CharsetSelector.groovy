package de.diedavids.cuba.dataimport.web.util


import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets

@Component('ddcdi_CharsetSelector')
@CompileStatic
class CharsetSelector {


    Map<String, Object> getCharsetFieldOptions() {
        Map<String, Object> options = new TreeMap<>()

        options.put(StandardCharsets.UTF_8.displayName(), StandardCharsets.UTF_8.name())
        options.put(StandardCharsets.UTF_16.displayName(), StandardCharsets.UTF_16.name())
        options.put(StandardCharsets.UTF_16BE.displayName(), StandardCharsets.UTF_16BE.name())
        options.put(StandardCharsets.UTF_16LE.displayName(), StandardCharsets.UTF_16LE.name())
        options.put(StandardCharsets.US_ASCII.displayName(), StandardCharsets.US_ASCII.name())
        options.put(StandardCharsets.ISO_8859_1.displayName(), StandardCharsets.ISO_8859_1.name())

        options
    }

}