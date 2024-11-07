package com.github.set08103_group_17.set08103_coursework;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class CityTest {
    private City city;

    @BeforeEach
    void setUp() {
        city = new City(1, "Test City", "TST", "Test District", 1000000);
    }

    @Test
    @DisplayName("Test City constructor and getters")
    void testCityConstructorAndGetters() {
        assertEquals(1, city.getID(), "ID should match constructor value");
        assertEquals("Test City", city.getName(), "Name should match constructor value");
        assertEquals("TST", city.getCode(), "Code should match constructor value");
        assertEquals("Test District", city.getDistrict(), "District should match constructor value");
        assertEquals(1000000, city.getPopulation(), "Population should match constructor value");
    }
}
