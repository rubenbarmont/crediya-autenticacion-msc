package co.com.crediya.passwordencoder;

import co.com.crediya.usecase.service.PasswordEncoderGateway;
import co.com.crediya.usecase.service.PasswordVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
// AHORA IMPLEMENTA AMBAS INTERFACES
public class BcryptPasswordVerifierAdapter implements PasswordVerifier, PasswordEncoderGateway {

    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<Boolean> verify(String rawPassword, String encodedPassword) {
        return Mono.fromCallable(() -> passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Override
    public Mono<String> encode(String rawPassword) {
        return Mono.fromCallable(() -> passwordEncoder.encode(rawPassword));
    }
}