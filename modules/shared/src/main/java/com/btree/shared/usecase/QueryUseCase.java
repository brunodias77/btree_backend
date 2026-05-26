package com.btree.shared.usecase;

/**
 * Caso de uso somente-leitura (query).
 * <p>
 * Diferente de {@link UseCase} (que retorna {@code Either} para capturar erros de validação),
 * queries não têm erros de validação — elas ou retornam o resultado ou lançam uma exceção
 * (ex: {@code NotFoundException}). Por isso retornam {@code OUT} diretamente.
 * Permite que a infraestrutura aplique otimizações como transações read-only.
 *
 * @param <IN>  Tipo do objeto de entrada (critérios da consulta)
 * @param <OUT> Tipo do objeto de saída (resultado da consulta)
 */
public interface QueryUseCase<IN, OUT> {
    OUT execute(IN in);
}

