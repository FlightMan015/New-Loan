package de.joonko.loan.partner.consors;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.util.JsonPathEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class ConsorsLinkExtractor {

    private final ConsorsPropertiesConfig consorsPropertiesConfig;

    private final JsonPathEvaluator jsonPathEvaluator;
    static final TypeRef<List<LinkRelation>> LIST_LINK_TYPE_REF = new TypeRef<>() {
    };

    /**
     * Extract the validation rules Link from Consors products JSON
     *
     * @param productsJson of type https://green-1.consorsfinanz.de/ratanet-api-test/pages/ratanet-api.html#Products
     */
    Optional<Link> validationRulesLink(String productsJson) {
        String classicLoanProductPath = "$['products']['1998_810']";
        return getLink(productsJson, "_validationrules", classicLoanProductPath);
    }

    /**
     * Extract the validation subscription Link from Consors validate rules JSON
     *
     * @param validationRulesJson of type https://green-1.consorsfinanz.de/ratanet-api-test/pages/ratanet-api.html#ValidationRules
     */
    Optional<Link> validationSubscriptionLink(String validationRulesJson) {
        return getLink(validationRulesJson, "_validatesubscription");

    }

    /**
     * Extract the personalized calculations Link from Consors validate subscription JSON
     *
     * @param validateSubscriptionJson of type https://green-1.consorsfinanz.de/ratanet-api-test/pages/ratanet-api.html#ValidateSubscriptionResponse
     */
    Optional<Link> personalizedCalculationsLink(String validateSubscriptionJson) {
        return getLink(validateSubscriptionJson, "_personalizedcalculations");
    }

    private Optional<Link> getLink(String json, String linkRelation, String path) {
        String jsonPathLinks = path + "['_links']";
        try {
            List<LinkRelation> links = jsonPathEvaluator.read(json, jsonPathLinks, LIST_LINK_TYPE_REF);
            return links.stream().filter(it -> it.hasRel(linkRelation)).findFirst().map(
                    cl -> new Link(consorsPropertiesConfig.getHost() + consorsPropertiesConfig.getRatanet(), cl.getHref(), cl.getMethod()));
        } catch (PathNotFoundException e) {
            log.error("Path not found for: " + jsonPathLinks, e);
            return Optional.empty();
        }
    }

    private Optional<Link> getLink(String json, String linkRelation) {
        return getLink(json, linkRelation, "");
    }

}
