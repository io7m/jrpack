/*
 * Copyright © 2012 http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jrpack.examples;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jrpack.Pack1D;
import com.io7m.jrpack.PackResult;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackResultCode;

public final class Main1D
{
  public static void main(
    final String args[])
    throws ConstraintError
  {
    // Create a 32x32 rectangle, holding rectangles of height 16
    final Pack1D packer = new Pack1D(32, 32, 16);

    final PackResult r0 = packer.insert(33);
    switch (r0.type) {
      case PACK_RESULT_OK:
        throw new AssertionError("unreachable");
      case PACK_RESULT_OUT_OF_SPACE:
        throw new AssertionError("unreachable");
      case PACK_RESULT_TOO_LARGE:
        System.out.println("too large!");
    }

    final PackResult r1 = packer.insert(32);
    switch (r1.type) {
      case PACK_RESULT_OK:
      {
        final PackOK r = (PackOK) r1;
        System.out.println(r.rectangle);
        break;
      }
      case PACK_RESULT_OUT_OF_SPACE:
        throw new AssertionError("unreachable");
      case PACK_RESULT_TOO_LARGE:
        throw new AssertionError("unreachable");
    }

    final PackResult r2 = packer.insert(32);
    assert r2.type == PackResultCode.PACK_RESULT_OK;

    final PackResult r3 = packer.insert(32);
    switch (r3.type) {
      case PACK_RESULT_OK:
        throw new AssertionError("unreachable");
      case PACK_RESULT_OUT_OF_SPACE:
        System.out.println("out of space!");
        break;
      case PACK_RESULT_TOO_LARGE:
        throw new AssertionError("unreachable");
    }
  }
}
