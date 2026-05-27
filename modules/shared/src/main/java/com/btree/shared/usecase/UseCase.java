package com.btree.shared.usecase;

/**
 * Contrato base para casos de uso de escrita da aplicação.
 *
 * <p>Retorna {@link UseCaseResponse}: output em caso de sucesso ou Notification
 * com erros de validacao/regra de negocio em caso de falha.
 *
 * @param <IN>  Tipo do Command/Input (imutável, geralmente um {@code record})
 * @param <OUT> Tipo do Output (imutável, geralmente um {@code record})
 */
public interface UseCase<IN, OUT> {
    UseCaseResponse<OUT> execute(IN in);
}
