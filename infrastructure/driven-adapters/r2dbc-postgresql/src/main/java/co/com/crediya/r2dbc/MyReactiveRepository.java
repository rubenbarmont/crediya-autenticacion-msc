package co.com.crediya.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface MyReactiveRepository extends ReactiveCrudRepository<UserData, UUID>, ReactiveQueryByExampleExecutor<UserData> {
    Mono<UserData> findByEmail(String email);
}
