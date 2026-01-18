package com.modolus.util.result;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ResultErrorSwitch<A, B, Y> {

    private final Result<A, B> given;
    private Result<A, Y> result = null;

    public ResultErrorSwitch<A, B, Y> caseError(B expected, Y value) {
        return caseError(expected, _ -> value);
    }

    public ResultErrorSwitch<A, B, Y> caseError(B expected, Function<B, Y> mapper) {
        if (given.isFailure() && Objects.equals(given.getError(), expected)) {
            result = Result.failure(mapper.apply(expected));
        }
        return this;
    }

    public ResultErrorSwitch<A, B, Y> otherwise(Y value) {
        return otherwise(_ -> value);
    }

    public ResultErrorSwitch<A, B, Y> otherwise(Function<B, Y> mapper) {
        if (given.isFailure()) {
            result = Result.failure(mapper.apply(given.getError()));
        }
        return this;
    }

    public Result<A, Y> finish() {
        if (given.isSuccess()) return Result.success(given.get());

        if (result == null)
            throw new UnsupportedOperationException("No case matched for error " + given.getError());
        return result;
    }

}
