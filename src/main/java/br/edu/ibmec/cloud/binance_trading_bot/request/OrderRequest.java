package br.edu.ibmec.cloud.binance_trading_bot.request;

import lombok.Data;

@Data
public class OrderRequest {
    private String simbolo;
    private String lado;
    private double quantidade;
    private double preco;       // para ordens LIMIT
    private Double stopPrice;   // para STOP LOSS e TAKE PROFIT
}
