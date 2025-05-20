package br.edu.ibmec.cloud.binance_trading_bot.controller;

import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserTrackingTicker;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.response.TickerResponse;
import br.edu.ibmec.cloud.binance_trading_bot.service.BinanceIntegration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("{id}/tickers")
public class TickerController {

    @Autowired
    private UserRepository repositorioUsuario;

    @Autowired
    private BinanceIntegration integracaoBinance;

    @GetMapping
    public ResponseEntity<List<TickerResponse>> listarTickers(@PathVariable("id") int usuarioId) {
        Optional<User> opcionalUsuario = this.repositorioUsuario.findById(usuarioId);

        if (opcionalUsuario.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User usuario = opcionalUsuario.get();
        ArrayList<String> listaSimbolos = new ArrayList<>();

        for (UserTrackingTicker item : usuario.getTickersMonitorados()) {
            listaSimbolos.add(item.getSimbolo());
        }

        this.integracaoBinance.setAPI_KEY(usuario.getBinanceApiKey());
        this.integracaoBinance.setSECRET_KEY(usuario.getBinanceSecretKey());

        String jsonResposta = this.integracaoBinance.getTickers(listaSimbolos);

        ObjectMapper mapeador = new ObjectMapper();
        mapeador.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            List<TickerResponse> listaResposta = mapeador.readValue(
                    jsonResposta,
                    new TypeReference<List<TickerResponse>>() {}
            );
            return new ResponseEntity<>(listaResposta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}