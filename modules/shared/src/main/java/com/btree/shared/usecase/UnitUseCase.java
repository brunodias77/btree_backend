package com.btree.shared.usecase;

/**
 * Contrato para casos de uso sem valor de retorno significativo (side-effect only).
 *
 * <p>Retorna {@link UseCaseResponse}: sucesso sem output significativo ou
 * Notification com erros em caso de falha.
 *
 * @param <IN> Tipo do Command/Input
 */
public interface UnitUseCase<IN> {
    UseCaseResponse<Void> execute(IN in);
}

