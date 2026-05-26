package com.btree.shared.validation;

/**
 * Representa um erro de validação com uma mensagem descritiva.
 *
 * Usado pelo {@link ValidationHandler} para acumular ou lançar erros de domínio.
 * Por ser um record, é imutável e seu valor é inteiramente definido pela mensagem.
 */
public record Error(String message) {
}