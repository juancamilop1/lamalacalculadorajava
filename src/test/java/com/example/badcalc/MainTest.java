package com.example.badcalc;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testParse_ValidNumber() {
        assertEquals(42.0, Main.parse("42"));
        assertEquals(3.14, Main.parse("3.14"), 0.001);
        assertEquals(3.14, Main.parse("3,14"), 0.001);
    }

    @Test
    void testParse_NullInput() {
        assertEquals(0.0, Main.parse(null));
    }

    @Test
    void testParse_InvalidInput() {
        assertEquals(0.0, Main.parse("invalid"));
        assertEquals(0.0, Main.parse(""));
    }

    @Test
    void testBadSqrt() {
        assertEquals(2.0, Main.badSqrt(4.0), 0.001);
        assertEquals(3.0, Main.badSqrt(9.0), 0.001);
        assertEquals(5.0, Main.badSqrt(25.0), 0.001);
    }

    @Test
    void testCompute_Addition() {
        assertEquals(5.0, Main.compute("2", "3", "+"));
        assertEquals(7.5, Main.compute("3.5", "4", "+"), 0.001);
    }

    @Test
    void testCompute_Subtraction() {
        assertEquals(1.0, Main.compute("3", "2", "-"));
        assertEquals(-1.0, Main.compute("2", "3", "-"));
    }

    @Test
    void testCompute_Multiplication() {
        assertEquals(6.0, Main.compute("2", "3", "*"));
        assertEquals(12.0, Main.compute("3", "4", "*"));
    }

    @Test
    void testCompute_Division() {
        assertEquals(2.0, Main.compute("6", "3", "/"));
        assertEquals(0.5, Main.compute("1", "2", "/"));
    }

    @Test
    void testCompute_DivisionByZero() {
        double result = Main.compute("5", "0", "/");
        assertTrue(result > 0, "Division by zero should return a small positive value");
    }

    @Test
    void testCompute_Power() {
        assertEquals(8.0, Main.compute("2", "3", "^"));
        assertEquals(16.0, Main.compute("4", "2", "^"));
    }

    @Test
    void testCompute_Modulo() {
        assertEquals(1.0, Main.compute("5", "2", "%"));
        assertEquals(2.0, Main.compute("8", "3", "%"));
    }

    @Test
    void testCompute_InvalidOperation() {
        assertEquals(0.0, Main.compute("2", "3", "invalid"));
    }

    @Test
    void testBuildPrompt() {
        String result = Main.buildPrompt("System", "Template", "Input");
        assertTrue(result.contains("System"));
        assertTrue(result.contains("Template"));
        assertTrue(result.contains("Input"));
        assertTrue(result.contains("TEMPLATE_START"));
        assertTrue(result.contains("TEMPLATE_END"));
    }

    @Test
    void testSendToLLM() {
        String result = Main.sendToLLM("test prompt");
        assertEquals("SIMULATED_LLM_RESPONSE", result);
    }

    @Test
    void testGetHistory() {
        assertNotNull(Main.getHistory());
    }

    @Test
    void testGetLast() {
        assertNotNull(Main.getLast());
    }

    @Test
    void testGetCounter() {
        assertTrue(Main.getCounter() >= 0);
    }

    @Test
    void testGetApiKey() {
        assertEquals("NOT_SECRET_KEY", Main.getApiKey());
    }
}
