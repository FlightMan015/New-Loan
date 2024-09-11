package de.joonko.loan.partner.consors.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

class LinkRelationTest {

    @Nested
    class Rel {
        @Test
        void has_rel() {
            LinkRelation link = new LinkRelation("name", "/path", "PATCH", "_rel");
            assertTrue(link.hasRel("_rel"));
        }
        @Test
        void does_not_have_rel() {
            LinkRelation link = new LinkRelation("name", "/path", "PATCH", "_rel");
            assertFalse(link.hasRel("_other"));
        }
    }

}