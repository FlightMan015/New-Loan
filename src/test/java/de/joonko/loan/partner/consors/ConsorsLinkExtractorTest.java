package de.joonko.loan.partner.consors;

import com.jayway.jsonpath.PathNotFoundException;
import de.joonko.loan.partner.consors.model.LinkRelation;
import de.joonko.loan.util.JsonPathEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.joonko.loan.partner.consors.ConsorsLinkExtractor.LIST_LINK_TYPE_REF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsorsLinkExtractorTest {

    private ConsorsLinkExtractor linkExtractor;

    private ConsorsPropertiesConfig config;

    @Mock
    private JsonPathEvaluator jsonPathEvaluator;

    private List<LinkRelation> givenLinkRelations;

    @BeforeEach
    void setUp() {
        config = new ConsorsPropertiesConfig();
        config.setHost("https://consors.example");
        config.setRatanet("/ratanet/path");
        config.setVersion("3.45");
        linkExtractor = new ConsorsLinkExtractor(config, jsonPathEvaluator);
        givenLinkRelations = Arrays.asList(
                new LinkRelation(
                        "name",
                        "/validation-rules",
                        "GET",
                        "_validationrules"
                ),
                new LinkRelation(
                        "name",
                        "/validate-subscription",
                        "POST",
                        "_validatesubscription"
                ),
                new LinkRelation(
                        "name",
                        "/personalized-calculations",
                        "PUT",
                        "_personalizedcalculations"
                )
        );

    }

    @Test
    void validationRulesLink() {
        when(jsonPathEvaluator.read("json", "$['products']['1998_810']['_links']", LIST_LINK_TYPE_REF)).thenReturn(givenLinkRelations);

        Optional<Link> link = linkExtractor.validationRulesLink("json");

        Link expected = new Link(
                "https://consors.example/ratanet/path",
                "/validation-rules",
                "GET");
        assertThat(link).hasValue(expected);
    }
    @Test
    void validationSubscriptionLink() {
        when(jsonPathEvaluator.read("json", "['_links']", LIST_LINK_TYPE_REF)).thenReturn(givenLinkRelations);

        Optional<Link> optionalLink = linkExtractor.validationSubscriptionLink("json");

        Link expected = new Link(
                "https://consors.example/ratanet/path",
                "/validate-subscription",
                "POST");
        assertThat(optionalLink).hasValue(expected);
    }


    @Test
    void personalizedCalculationsLink() {
        when(jsonPathEvaluator.read("json", "['_links']", LIST_LINK_TYPE_REF)).thenReturn(givenLinkRelations);

        Optional<Link> optionalLink = linkExtractor.personalizedCalculationsLink("json");

        Link expected = new Link(
                "https://consors.example/ratanet/path",
                "/personalized-calculations",
                "PUT");
        assertThat(optionalLink).hasValue(expected);
    }

    @Nested
    class ErrorHandling {
        @Test
        void returnEmptyIfLinksAreMissing() {
            when(jsonPathEvaluator.read("json", "['_links']", LIST_LINK_TYPE_REF)).thenReturn(Collections.emptyList());

            Optional<Link> optionalLink = linkExtractor.validationSubscriptionLink("json");

            assertThat(optionalLink).isEmpty();
        }

        @Test
        void returnEmptyIfPathNotFound() {
            when(jsonPathEvaluator.read("json", "['_links']", LIST_LINK_TYPE_REF)).thenThrow(PathNotFoundException.class);

            Optional<Link> optionalLink = linkExtractor.validationSubscriptionLink("json");

            assertThat(optionalLink).isEmpty();
        }
    }

}