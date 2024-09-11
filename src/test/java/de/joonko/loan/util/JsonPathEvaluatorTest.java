package de.joonko.loan.util;

import com.jayway.jsonpath.TypeRef;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JsonPathEvaluator.class)
class JsonPathEvaluatorTest {

    @Autowired
    JsonPathEvaluator jsonPathEvaluator;

    @Test
    void can_parse_to_string() {
        String result = jsonPathEvaluator.read("{\"_links\": \"the-links\"}", "['_links']", new TypeRef<>() {
        });
        assertThat(result).isEqualTo("the-links");
    }

    @Test
    void can_parse_to_list() {
        List<String> result = jsonPathEvaluator.read("{\"_links\": [\"one\", \"two\", \"three\"]}", "['_links']", new TypeRef<>() {
        });
        assertThat(result).isEqualTo(Arrays.asList("one", "two", "three"));
    }
}