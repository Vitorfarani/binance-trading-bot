package br.edu.ibmec.cloud.binance_trading_bot.service;

import com.binance.connector.client.SpotClient;
import com.binance.connector.client.impl.SpotClientImpl;
import kotlin.contracts.Returns;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Data
public class BinanceIntegration {
    private String BASE_URL = "https://testnet.binance.vision";
    private String API_KEY;
    private String SECRET_KEY;

    public String getTickers(ArrayList<String> simbolo) {
        SpotClient client = new SpotClientImpl(this.API_KEY, this.SECRET_KEY, this.BASE_URL);
        Map<String, Object> parametro = new LinkedHashMap<>();
        parametro.put("símbolo ", simbolo);
        String result = client.createMarket().ticker(parametro);
        return result;
    }

    public String createMarketOrder(String simbolo, double quantidade, String lado) {
        SpotClient client = new SpotClientImpl(this.API_KEY, this.SECRET_KEY, this.BASE_URL);

        Map<String, Object> parametro = new LinkedHashMap<>();
        parametro.put("símbolo", simbolo);
        parametro.put("lado", lado);
        parametro.put("tipo", "MARKET");
        parametro.put("quantidade", quantidade);
        String result = client.createTrade().newOrder(parametro);
        return result;

    }
}