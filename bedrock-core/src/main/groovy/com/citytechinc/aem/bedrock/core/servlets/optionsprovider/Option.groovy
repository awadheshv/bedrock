package com.citytechinc.aem.bedrock.core.servlets.optionsprovider

import groovy.transform.Immutable

/**
 * Text/value pair for displaying in a selection dialog widget, used in conjunction with the
 * <code>AbstractOptionsProviderServlet</code>.
 */
@Immutable
class Option {

    static def ALPHA = new Comparator<Option>() {
        @Override
        int compare(Option option1, Option option2) {
            option1.text.compareTo(option2.text)
        }
    }

    static def ALPHA_IGNORE_CASE = new Comparator<Option>() {
        @Override
        int compare(Option option1, Option option2) {
            option1.text.compareToIgnoreCase(option2.text)
        }
    }

    String value

    String text

    /**
     * @param map map where key=[option value] and value=[option text]
     * @list of options created from map
     */
    static List<Option> fromMap(Map<String, String> map) {
        map.collect { value, text -> new Option(value, text) } as List
    }
}
