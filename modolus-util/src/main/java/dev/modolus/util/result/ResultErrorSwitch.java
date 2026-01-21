/*
 * Copyright (C) 2026 Modolus-Framework
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package dev.modolus.util.result;

import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ResultErrorSwitch<
    A, B extends Enum<B> & ErrorType<B>, Y extends Enum<Y> & ErrorType<Y>> {

  private final Result<A, B> given;
  private Result<A, Y> result = null;

  public ResultErrorSwitch<A, B, Y> caseError(B expected, ErrorMapper<B, Y> mapper) {
    if (given.isFailure() && Objects.equals(given.getError().getErrorType(), expected)) {
      result = Result.failure(mapper.apply(given.getError()));
    }
    return this;
  }

  public ResultErrorSwitch<A, B, Y> otherwise(Error<Y> value) {
    return otherwise(_ -> value);
  }

  public ResultErrorSwitch<A, B, Y> otherwise(ErrorMapper<B, Y> mapper) {
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
