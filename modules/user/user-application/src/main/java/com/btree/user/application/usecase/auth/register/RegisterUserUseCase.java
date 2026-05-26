package com.btree.user.application.usecase.auth.register;

import com.btree.shared.usecase.UseCase;
import com.btree.shared.validation.Notification;
import io.vavr.control.Either;

public class RegisterUserUseCase implements UseCase<RegisterUserInput, RegisterUserOutput> {
    @Override
    public Either<Notification, RegisterUserOutput> execute(RegisterUserInput registerUserInput) {
        return null;
    }
}
