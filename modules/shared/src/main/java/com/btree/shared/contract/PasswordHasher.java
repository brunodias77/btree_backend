package com.btree.shared.contract;

/**
 * Porta para hash e verificação de senhas.
 * Implementação: BCryptPasswordHasher em infrastructure.
 */
public interface PasswordHasher {

    /**
     * Gera o hash da senha em texto claro.
     *
     * @param rawPassword senha em texto claro fornecida pelo usuário
     * @return hash pronto para armazenamento (inclui salt e fator de custo)
     */
    String hash(String rawPassword);

    /**
     * Verifica se a senha em texto claro corresponde ao hash armazenado.
     *
     * @param rawPassword    senha em texto claro a verificar
     * @param hashedPassword hash armazenado no banco
     * @return {@code true} se a senha bate com o hash
     */
    boolean matches(String rawPassword, String hashedPassword);
}
