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

package com.io7m.jrpack;

import junit.framework.Assert;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jrpack.Pack1D;
import com.io7m.jrpack.PackResult;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackResultCode;

public class Pack1DTest
{
  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testInvalidElementHeight0()
      throws ConstraintError
  {
    new Pack1D(2, 2, 0);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testInvalidElementHeightLarge()
      throws ConstraintError
  {
    new Pack1D(2, 2, 3);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testInvalidHeight0()
      throws ConstraintError
  {
    new Pack1D(1, 0, 1);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testInvalidWidth0()
      throws ConstraintError
  {
    new Pack1D(0, 128, 1);
  }

  @Test public void testPackValid()
    throws ConstraintError
  {
    final Pack1D p = new Pack1D(32, 32, 8);
    Assert.assertEquals(32, p.getHeight());
    Assert.assertEquals(32, p.getWidth());
    Assert.assertEquals(31, p.getUpperX());
    Assert.assertEquals(31, p.getUpperY());

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackResult.PackOK rok = (PackOK) r;
      Assert.assertEquals(0, rok.rectangle.x0);
      Assert.assertEquals(0, rok.rectangle.y0);
      Assert.assertEquals(31, rok.rectangle.x1);
      Assert.assertEquals(7, rok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackResult.PackOK rok = (PackOK) r;
      Assert.assertEquals(0, rok.rectangle.x0);
      Assert.assertEquals(8, rok.rectangle.y0);
      Assert.assertEquals(31, rok.rectangle.x1);
      Assert.assertEquals(15, rok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackResult.PackOK rok = (PackOK) r;
      Assert.assertEquals(0, rok.rectangle.x0);
      Assert.assertEquals(16, rok.rectangle.y0);
      Assert.assertEquals(31, rok.rectangle.x1);
      Assert.assertEquals(23, rok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackResult.PackOK rok = (PackOK) r;
      Assert.assertEquals(0, rok.rectangle.x0);
      Assert.assertEquals(24, rok.rectangle.y0);
      Assert.assertEquals(31, rok.rectangle.x1);
      Assert.assertEquals(31, rok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OUT_OF_SPACE, r.type);
    }

    {
      final PackResult r = p.insert(32);
      Assert.assertEquals(PackResultCode.PACK_RESULT_OUT_OF_SPACE, r.type);
    }
  }

  @Test public void testTooLarge()
    throws ConstraintError
  {
    final Pack1D p = new Pack1D(32, 32, 2);
    final PackResult r = p.insert(33);
    Assert.assertEquals(PackResultCode.PACK_RESULT_TOO_LARGE, r.type);
  }

  @SuppressWarnings("unused") @Test public void testValid()
    throws ConstraintError
  {
    new Pack1D(32, 32, 2);
  }

  @Test(expected = ConstraintError.class) public void testZeroWidth()
    throws ConstraintError
  {
    final Pack1D p = new Pack1D(32, 32, 2);
    p.insert(0);
  }
}
