package com.io7m.jrpack;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackResultCode;

public class Pack2DTest
{
  @Test public void testExact()
    throws ConstraintError
  {
    final Pack2D p = new Pack2D(new Rectangle(0, 0, 15, 15));

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 7, 7));
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackOK ok = (PackOK) r;
      Assert.assertEquals(0, ok.rectangle.x0);
      Assert.assertEquals(7, ok.rectangle.x1);
      Assert.assertEquals(0, ok.rectangle.y0);
      Assert.assertEquals(7, ok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 7, 7));
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackOK ok = (PackOK) r;
      Assert.assertEquals(8, ok.rectangle.x0);
      Assert.assertEquals(15, ok.rectangle.x1);
      Assert.assertEquals(0, ok.rectangle.y0);
      Assert.assertEquals(7, ok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 7, 7));
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackOK ok = (PackOK) r;
      Assert.assertEquals(0, ok.rectangle.x0);
      Assert.assertEquals(7, ok.rectangle.x1);
      Assert.assertEquals(8, ok.rectangle.y0);
      Assert.assertEquals(15, ok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 7, 7));
      Assert.assertEquals(PackResultCode.PACK_RESULT_OK, r.type);
      final PackOK ok = (PackOK) r;
      Assert.assertEquals(8, ok.rectangle.x0);
      Assert.assertEquals(15, ok.rectangle.x1);
      Assert.assertEquals(8, ok.rectangle.y0);
      Assert.assertEquals(15, ok.rectangle.y1);
    }

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 7, 7));
      Assert.assertEquals(PackResultCode.PACK_RESULT_OUT_OF_SPACE, r.type);
    }
  }

  @Test public void testHeightTooLarge()
    throws ConstraintError
  {
    final Pack2D p = new Pack2D(new Rectangle(0, 0, 31, 15));

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 16, 16));
      Assert.assertEquals(PackResultCode.PACK_RESULT_TOO_LARGE, r.type);
    }
  }

  @Test public void testProperties()
    throws ConstraintError
  {
    final Pack2D p = new Pack2D(new Rectangle(0, 0, 15, 15));

    Assert.assertEquals(16, p.getWidth());
    Assert.assertEquals(16, p.getHeight());
    Assert.assertEquals(15, p.getUpperX());
    Assert.assertEquals(15, p.getUpperY());
  }

  @Test public void testWidthTooLarge()
    throws ConstraintError
  {
    final Pack2D p = new Pack2D(new Rectangle(0, 0, 15, 31));

    {
      final PackResult r = p.insert(new Rectangle(0, 0, 16, 16));
      Assert.assertEquals(PackResultCode.PACK_RESULT_TOO_LARGE, r.type);
    }
  }
}
