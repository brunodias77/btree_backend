package com.btree.user.domain.validator;

import com.btree.shared.validation.Error;
import com.btree.shared.validation.ValidationHandler;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UserValidatorTest {

    @Test
    void DEVE_VALIDAR_SENHA_FORTE_SEM_ERROS() {
        final var handler = mock(ValidationHandler.class);

        UserValidator.validatePassword("Senha@123", handler);

        verify(handler, never()).append(org.mockito.ArgumentMatchers.any(Error.class));
    }

    @Test
    void DEVE_REGISTRAR_ERROS_PARA_SENHA_FRACA() {
        final var handler = mock(ValidationHandler.class);

        UserValidator.validatePassword("abc", handler);

        verify(handler).append(argThat((Error error) -> error.message().contains("mínimo 8")));
        verify(handler).append(argThat((Error error) -> error.message().contains("letra maiúscula")));
        verify(handler).append(argThat((Error error) -> error.message().contains("dígito numérico")));
        verify(handler).append(argThat((Error error) -> error.message().contains("caractere especial")));
    }
}
