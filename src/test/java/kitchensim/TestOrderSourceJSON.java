package kitchensim;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TestOrderSourceJSON {

    @Test
    public void smokeTestForOrderSourceJSON() throws Exception {
        assertDoesNotThrow(() -> OrderSourceJSON.create(null));
    }
}
