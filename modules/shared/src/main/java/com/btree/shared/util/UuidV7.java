package com.btree.shared.util;
import java.security.SecureRandom;
import java.util.UUID;


public final class UuidV7 {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UuidV7() {}

    /** Gera um novo UUID v7. */
    public static UUID generate() {
        final long now = System.currentTimeMillis();

        // 48 bits de timestamp + 4 bits de versão + 12 bits aleatórios
        final long msb = (now << 16)
                | (0x7000L)                          // versão 7
                | (RANDOM.nextLong() & 0x0FFFL);     // 12 bits aleatórios

        // 2 bits de variante (10) + 62 bits aleatórios
        final long lsb = (RANDOM.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL)
                | 0x8000_0000_0000_0000L;             // variante RFC 4122

        return new UUID(msb, lsb);
    }
}
