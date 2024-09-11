package de.joonko.loan.partner.consors;

import com.jayway.jsonpath.TypeRef;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.util.JsonPathEvaluator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = JsonPathEvaluator.class)
class JsonPathEvaluatorForLinkRelationsTest {

    @Autowired
    JsonPathEvaluator jsonPathEvaluator;

    @Test
    void can_parse_to_list_of_link_relation() {
        List<LinkRelation> result = jsonPathEvaluator.read(
                ConsorsFixtures.PRODUCT_RESPONSE,
                "$['products']['1998_810']['_links']",
                new TypeRef<>() {
                });

        assertThat(result).containsExactlyInAnyOrder(
                new LinkRelation("Financial Calculations", "/partner/freie_verfuegung/financialcalculations?version=5.0", "GET", "_financialcalculations"),
                new LinkRelation("Representative Example", "/partner/freie_verfuegung/financialcalculations/sample?version=5.0", "GET", "_financialcalculations/sample"),
                new LinkRelation("Validation Rules", "/partner/freie_verfuegung/validationrules?version=5.0", "GET", "_validationrules"),
                new LinkRelation("Authenticate Access Token", "/partner/neu_test/accessToken?version=5.0", "POST", "_accessToken")
        );
    }

}