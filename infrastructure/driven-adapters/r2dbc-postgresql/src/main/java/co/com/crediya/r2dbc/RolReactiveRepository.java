package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.RolEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, Long>, ReactiveQueryByExampleExecutor<RolEntity> {

    Mono<RolEntity> findByName(String name);
}