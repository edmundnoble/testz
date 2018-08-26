/*
 * Copyright (c) 2018, Edmund Noble
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package testz

/**
  The most boring test `Harness` you can think of:
  pure tests, with sections.
  Any harness type should be convertible to a `Harness`;
  it's the lingua franca of tests. If you write tests using
  `Harness`, they can be adapted to work with any suite type later.
*/
abstract class Harness[T] {
  def test(name: String)(assertions: () => Result): T

  def section(name: String)(test1: T, tests: T*): T
}

/**
  A test harness with test results in the effect `F[_]`.
 */
abstract class EffectHarness[F[_], T] {
  def test(name: String)(assertions: () => F[Result]): T

  def section(name: String)(test1: T, tests: T*): T
}

object EffectHarness {
  def toHarness[F[_], T](self: EffectHarness[F, T])(pure: Result => F[Result]): Harness[T] = new Harness[T] {
    def test
      (name: String)
      (assertions: () => Result)
      : T = self.test(name)(() => pure(assertions()))

    def section
      (name: String)
      (test1: T, tests: T*)
      : T = self.section(name)(test1, tests: _*)
    }
}

/**
  A type for test results.
  A two-branch sum, either `Succeed()`, or `Fail()`.
*/
sealed abstract class Result

final class Fail private() extends Result {
  override def toString(): String = "Fail()"
  override def equals(other: Any): Boolean = other.asInstanceOf[AnyRef] eq this
}

object Fail {
  private val cached = new Fail()

  def apply(): Result = cached
}

final class Succeed private() extends Result {
  override def toString(): String = "Succeed()"
  override def equals(other: Any): Boolean = other.asInstanceOf[AnyRef] eq this
}

object Succeed {
  private val cached = new Succeed()

  def apply(): Result = cached
}

object Result {
  def combine(first: Result, second: Result): Result =
    if (first eq second) first
    else Fail()
}