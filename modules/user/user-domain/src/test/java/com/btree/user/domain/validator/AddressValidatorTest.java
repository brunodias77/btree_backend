package com.btree.user.domain.validator;

import com.btree.shared.validation.Error;
import com.btree.shared.validation.ValidationHandler;
import com.btree.user.domain.entity.Address;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AddressValidatorTest {

    @Test
    void DEVE_REGISTRAR_ERROS_DO_ENDERECO_INVALIDO() {
        final var address = Address.with(
                java.util.UUID.randomUUID(),
                null,
                "Casa",
                "Cliente",
                "",
                "123",
                null,
                null,
                "",
                "sp",
                "abc",
                "",
                null,
                null,
                null,
                false,
                false,
                null,
                null,
                null);
        final var handler = mock(ValidationHandler.class);

        address.validate(handler);

        verify(handler).append(argThat((Error error) -> error.message().contains("'userId'")));
        verify(handler).append(argThat((Error error) -> error.message().contains("'street'")));
        verify(handler).append(argThat((Error error) -> error.message().contains("'city'")));
        verify(handler).append(argThat((Error error) -> error.message().contains("'state'")));
        verify(handler).append(argThat((Error error) -> error.message().contains("'postalCode'")));
        verify(handler).append(argThat((Error error) -> error.message().contains("'country'")));
    }
}
