package br.edu.ibmec.cloud.binance_trading_bot.response;

import lombok.Data;

@Data
public class TickerResponse {

    private String simbolo;

    private double ultimoPreco;
}