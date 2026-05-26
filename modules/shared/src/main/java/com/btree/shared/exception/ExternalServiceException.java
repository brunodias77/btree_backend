package com.btree.shared.exception;

import com.btree.shared.validation.Error;

import java.util.List;

/**
 * Exceção lançada quando uma chamada a serviço externo falha (HTTP 502/503).
 * Exemplos: falha no envio de e-mail, erro no gateway de pagamento, falha no serviço fiscal.
 */
public class ExternalServiceException extends RuntimeException {

    private final String serviceName;

    protected ExternalServiceException(final String serviceName, final String message, final Throwable cause) {
        super("[%s] %s".formatted(serviceName, message), cause);
        this.serviceName = serviceName;
    }

    public static ExternalServiceException of(final String serviceName, final String message) {
        return new ExternalServiceException(serviceName, message, null);
    }

    public static ExternalServiceException of(final String serviceName, final String message, final Throwable cause) {
        return new ExternalServiceException(serviceName, message, cause);
    }

    public String getServiceName() {
        return serviceName;
    }
}
