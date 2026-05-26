package com.btree.shared.contract;

/**
 * Contrato para criptografia simétrica reversível de strings sensíveis em repouso.
 * Diferente de {@link TokenHasher} (one-way), este contrato permite decrypt.
 */
public interface StringEncryptor {

    /**
     * Cifra o valor em texto claro e retorna a representação criptografada.
     *
     * @param value texto claro a cifrar (ex: chave de API, token de integração)
     * @return string cifrada, segura para armazenamento em banco de dados
     */
    String encrypt(String value);

    /**
     * Decifra o valor criptografado e retorna o texto claro original.
     *
     * @param value string cifrada previamente por {@link #encrypt}
     * @return texto claro original
     */
    String decrypt(String value);
}

