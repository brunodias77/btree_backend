package com.btree.shared.job;

import com.btree.shared.validation.Notification;
import io.vavr.control.Either;

public interface Job<IN> {
    Either<Notification, JobResult> execute(IN input);
}
