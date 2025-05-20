package br.edu.ibmec.cloud.binance_trading_bot.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class UserOrderReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String simbolo;

    @Column
    private double quantidade;

    @Column
    private double precoCompra;

    @Column
    private double precoVenda;

    @Column
    private LocalDateTime dataOperacao;
}
