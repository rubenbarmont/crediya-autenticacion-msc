package co.com.crediya.usecase.query.finduserbyid;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserNotFoundException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FindUserByIdUseCase {

    private final UserRepository userRepository;

    public Mono<User> execute(Long idUser) {
        return userRepository.findById(idUser)
                .switchIfEmpty(Mono.error(new UserNotFoundException(idUser)));
    }
}
