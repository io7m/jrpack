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
import com.io7m.jrpack.Pack2D;
import com.io7m.jrpack.PackResult;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.Rectangle;

public final class Main2D
{
  public static void main(
    final String args[])
    throws ConstraintError
  {
    // Create a 32x32 rectangle
    final Pack2D packer = new Pack2D(32, 32);

    final PackResult r0 = packer.insert(new Rectangle(0, 0, 63, 63));
    switch (r0.type) {
      case PACK_RESULT_OK:
        throw new AssertionError("unreachable");
      case PACK_RESULT_OUT_OF_SPACE:
        throw new AssertionError("unreachable");
      case PACK_RESULT_TOO_LARGE:
        System.out.println("too large!");
    }

    final PackResult r1 = packer.insert(new Rectangle(0, 0, 31, 31));
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

    final PackResult r3 = packer.insert(new Rectangle(0, 0, 31, 31));
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
