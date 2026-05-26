package com.btree.shared.contract;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


/**
 * Porta para emissão de notas fiscais eletrônicas (NF-e / NFS-e).
 *
 * <p>A implementação real se comunica com o provedor fiscal (ex: Focus NFe, Enotas).
 * Todos os tipos de dados necessários para a emissão estão definidos como records internos,
 * mantendo o contrato auto-contido e sem dependências externas.
 */
public interface FiscalService {

    /** Representa um item de linha do pedido incluído na nota fiscal. */
    record LineItem(
            String productName,
            String productSku,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}

    /** Dados necessários para solicitar a emissão de uma nota fiscal. */
    record FiscalServiceRequest(
            String orderId,
            String orderNumber,
            /** CPF ou CNPJ do comprador. */
            String buyerDocument,
            String buyerName,
            List<LineItem> items,
            BigDecimal total,
            String observations
    ) {}

    /** Resultado retornado pelo provedor fiscal após a emissão bem-sucedida. */
    record FiscalServiceResult(
            String invoiceNumber,
            /** URL para visualização da DANFE ou NFS-e em PDF. */
            String invoiceUrl,
            /** URL para download do XML da nota. */
            String xmlUrl,
            Instant issuedAt
    ) {}

    /**
     * Emite a nota fiscal com base nos dados do pedido.
     *
     * @param request dados do pedido e do comprador
     * @return resultado com número, URLs e timestamp da nota emitida
     */
    FiscalServiceResult issue(FiscalServiceRequest request);
}
