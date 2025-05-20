package br.edu.ibmec.cloud.binance_trading_bot.controller;

import br.edu.ibmec.cloud.binance_trading_bot.model.User;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserConfiguration;
import br.edu.ibmec.cloud.binance_trading_bot.model.UserTrackingTicker;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserConfigurationRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserRepository;
import br.edu.ibmec.cloud.binance_trading_bot.repository.UserTrackingTickerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository usuarioRepository;

    @Autowired
    private UserConfigurationRepository configuracaoRepository;

    @Autowired
    private UserTrackingTickerRepository tickerRepository;

    @PostMapping
    public ResponseEntity<User> criarUsuario(@RequestBody User usuario) {
        this.usuarioRepository.save(usuario);
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<User> buscarPorId(@PathVariable("id") Integer id) {
        return this.usuarioRepository.findById(id)
                .map(usuario -> new ResponseEntity<>(usuario, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("{id}/configuration")
    public ResponseEntity<User> associarConfiguracao(@PathVariable("id") Integer id, @RequestBody UserConfiguration configuracao) {
        Optional<User> usuarioOpcional = this.usuarioRepository.findById(id);
        if (usuarioOpcional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.configuracaoRepository.save(configuracao);
        User usuario = usuarioOpcional.get();
        usuario.getConfiguracoes().add(configuracao);
        usuarioRepository.save(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @PostMapping("{id}/tracking-ticker")
    public ResponseEntity<User> associarTicker(@PathVariable("id") Integer id, @RequestBody UserTrackingTicker ticker) {
        Optional<User> usuarioOpcional = this.usuarioRepository.findById(id);
        if (usuarioOpcional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        this.tickerRepository.save(ticker);
        User usuario = usuarioOpcional.get();
        usuario.getTickersMonitorados().add(ticker);
        usuarioRepository.save(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable("id") Integer id) {
        if (!usuarioRepository.existsById(id)) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        usuarioRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{id}/configuration/{configId}")
    public ResponseEntity<User> deletarConfiguracao(@PathVariable("id") Integer usuarioId, @PathVariable("configId") Integer configId) {
        Optional<User> usuarioOpcional = usuarioRepository.findById(usuarioId);
        Optional<UserConfiguration> configuracaoOpcional = configuracaoRepository.findById(configId);

        if (usuarioOpcional.isEmpty() || configuracaoOpcional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User usuario = usuarioOpcional.get();
        UserConfiguration configuracao = configuracaoOpcional.get();

        usuario.getConfiguracoes().remove(configuracao);
        configuracaoRepository.delete(configuracao);
        usuarioRepository.save(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }

    @DeleteMapping("{id}/tracking-ticker/{tickerId}")
    public ResponseEntity<User> deletarTicker(@PathVariable("id") Integer usuarioId, @PathVariable("tickerId") Integer tickerId) {
        Optional<User> usuarioOpcional = usuarioRepository.findById(usuarioId);
        Optional<UserTrackingTicker> tickerOpcional = tickerRepository.findById(tickerId);

        if (usuarioOpcional.isEmpty() || tickerOpcional.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        User usuario = usuarioOpcional.get();
        UserTrackingTicker ticker = tickerOpcional.get();

        usuario.getTickersMonitorados().remove(ticker);
        tickerRepository.delete(ticker);
        usuarioRepository.save(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.OK);
    }
}