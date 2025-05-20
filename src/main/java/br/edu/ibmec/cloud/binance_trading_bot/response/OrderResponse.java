package br.edu.ibmec.cloud.binance_trading_bot.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderResponse {

    private String simbolo;

    private String idOrdem;

    private BigDecimal quantidadeExecutada;

    private String tipo;

    private String lado;

    private List<OrderFillResponse> preenchimentos;
}