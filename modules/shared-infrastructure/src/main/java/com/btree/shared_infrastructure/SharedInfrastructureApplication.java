package com.btree.shared_infrastructure;

import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração do módulo de infraestrutura compartilhada.
 *
 * <p>Funciona como ponto de entrada para o Spring descobrir beans comuns a vários
 * módulos, como gateways de outbox, componentes transacionais e jobs de suporte.
 */
@Configuration
public class SharedInfrastructureApplication {
}
