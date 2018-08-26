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

import scalaz._, Scalaz._
import z._, z.streaming._

object PropertySuite {
  // TODO: perhaps there's a better way to test these than math.
  val testData = Unfold[Id, Int]((1 to 6): _*).flatMap {
    i =>
      val n = i * 10
      Unfold((n to (n + 5)): _*)
  }

  def tests[T](harness: Harness[T]): T = {
    import harness._
    section("int ranges")(
      test("exhaustiveS") { () =>
        val actualErrors = exhaustiveS[Id, Int](1, 2, 3, 4, 5, 6)(i =>
          if (i =/= 3) Fail()
          else Succeed()
        )
        assert(actualErrors === Fail())
      },
      test("exhaustiveU") { () =>
        val actualErrors = exhaustiveU[Id, Int](testData) { i =>
          if (i % 5 === 1) Fail()
          else Succeed()
        }
        assert(actualErrors === Fail())
      }
    )
  }
}