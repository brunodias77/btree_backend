package com.btree.shared.value_object;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoneyTest {

    @Test
    void DEVE_ARREDONDAR_COM_HALF_UP_E_USAR_MOEDA_PADRAO() {
        final var money = Money.of(new BigDecimal("10.235"));

        assertEquals(new BigDecimal("10.24"), money.getAmount());
        assertEquals("BRL", money.getCurrency());
    }

    @Test
    void DEVE_SOMAR_E_SUBTRAIR_MANTENDO_MOEDA() {
        final var total = Money.of(new BigDecimal("10.00"))
                .add(Money.of(new BigDecimal("2.50")))
                .subtract(Money.of(new BigDecimal("1.25")));

        assertEquals(new BigDecimal("11.25"), total.getAmount());
    }

    @Test
    void DEVE_REJEITAR_SUBTRACAO_COM_RESULTADO_NEGATIVO() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.of(new BigDecimal("1.00")).subtract(Money.of(new BigDecimal("2.00"))));
    }

    @Test
    void DEVE_REJEITAR_OPERACOES_COM_MOEDAS_DIFERENTES() {
        final var brl = Money.of(new BigDecimal("10.00"), "BRL");
        final var usd = Money.of(new BigDecimal("10.00"), "USD");

        assertThrows(IllegalArgumentException.class, () -> brl.add(usd));
    }

    @Test
    void DEVE_MULTIPLICAR_E_COMPARAR_VALORES() {
        final var money = Money.of(new BigDecimal("2.50")).multiply(3);

        assertEquals(new BigDecimal("7.50"), money.getAmount());
        assertTrue(money.isGreaterThan(Money.of(new BigDecimal("7.49"))));
        assertTrue(money.isGreaterThanOrEqual(Money.of(new BigDecimal("7.50"))));
        assertFalse(money.isZero());
    }
}
