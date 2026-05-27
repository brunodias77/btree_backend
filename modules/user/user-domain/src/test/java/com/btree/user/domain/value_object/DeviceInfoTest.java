package com.btree.user.domain.value_object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class DeviceInfoTest {

    @Test
    void DEVE_COMPARAR_DEVICE_INFO_POR_VALOR() {
        final var first = DeviceInfo.of("127.0.0.1", "JUnit");
        final var second = DeviceInfo.of("127.0.0.1", "JUnit");
        final var other = DeviceInfo.of("10.0.0.1", "JUnit");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, other);
    }

    @Test
    void DEVE_FORMATAR_TEXTO_DO_DEVICE_INFO() {
        final var deviceInfo = DeviceInfo.of("127.0.0.1", "JUnit");

        assertEquals("DeviceInfo{ip='127.0.0.1', userAgent='JUnit'}", deviceInfo.toString());
    }
}
