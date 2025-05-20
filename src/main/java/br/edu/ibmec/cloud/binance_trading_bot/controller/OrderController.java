package br.edu.ibmec.cloud.binance_trading_bot.controller;

import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserOrderReport;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserOrderReportRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.request.OrderRequest;
import br.edu.ibmec.cloud.binance_trading_bot.response.OrderResponse;
import br.edu.ibmec.cloud.binance_trading_bot.service.BinanceIntegration;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("{id}/order")
public class OrderController {

    @Autowired
    private UserRepository usuarioRepositorio;

    @Autowired
    private UserOrderReportRepository relatorioOrdemRepositorio;

    @Autowired
    private BinanceIntegration binanceServico;

    @PostMapping
    public ResponseEntity<OrderResponse> enviarOrdem(@PathVariable("id") int id, @RequestBody OrderRequest pedido) {
        Optional<User> usuarioOptional = this.usuarioRepositorio.findById(id);
        if (usuarioOptional.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User usuario = usuarioOptional.get();
        this.binanceServico.setAPI_KEY(usuario.getBinanceApiKey());
        this.binanceServico.setSECRET_KEY(usuario.getBinanceSecretKey());

        ObjectMapper mapeadorObjeto = new ObjectMapper();
        mapeadorObjeto.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            String resultado = this.binanceServico.createMarketOrder(pedido.getSimbolo(),
                    pedido.getQuantidade(),
                    pedido.getLado());
            OrderResponse resposta = mapeadorObjeto.readValue(resultado, OrderResponse.class);

            if ("BUY".equals(pedido.getLado())) {
                UserOrderReport relatorio = new UserOrderReport();
                relatorio .setSimbolo(pedido.getSimbolo());
                relatorio.setQuantidade(pedido.getQuantidade());
                relatorio.setPrecoCompra(resposta.getPreenchimentos().get(0).getPreco());
                relatorio.setDataOperacao(LocalDateTime.now());

                this.relatorioOrdemRepositorio.save(relatorio);

                usuario.getRelatoriosDeOrdens().add(relatorio);
                this.usuarioRepositorio.save(usuario);
            }

            if ("SELL".equals(pedido.getLado())) {
                UserOrderReport ordem = null;
                for (UserOrderReport item : usuario.getRelatoriosDeOrdens()) {
                    if (item.getSimbolo().equals(pedido.getSimbolo()) && item.getPrecoVenda() == 0) {
                        ordem = item;
                        break;
                    }
                }

                if (ordem != null) {
                    ordem.setPrecoVenda(resposta.getPreenchimentos().get(0).getPreco());
                    this.relatorioOrdemRepositorio.save(ordem);
                }
            }

            return new ResponseEntity<>(resposta, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}