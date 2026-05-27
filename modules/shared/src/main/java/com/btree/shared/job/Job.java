package com.btree.shared.job;

import com.btree.shared.usecase.UseCaseResponse;

public interface Job<IN> {
    UseCaseResponse<JobResult> execute(IN input);
}
