package com.io7m.jrpack;

import junit.framework.Assert;

import org.junit.Test;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jrpack.Rectangle.Fit;

public class RectangleTest
{
  @Test public void testRectangle0()
    throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);

    Assert.assertTrue(r.getHeight() == 1);
    Assert.assertTrue(r.getWidth() == 1);
    Assert.assertTrue(r.getUpperX() == 0);
    Assert.assertTrue(r.getUpperY() == 0);
    Assert.assertTrue(r.x0 == 0);
    Assert.assertTrue(r.y0 == 0);
  }

  @Test public void testRectangleEquals()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 1, 1);
    final Rectangle r1 = new Rectangle(0, 0, 1, 1);
    final Rectangle r2 = new Rectangle(1, 0, 2, 1);
    final Rectangle r3 = new Rectangle(0, 1, 1, 2);
    final Rectangle r4 = new Rectangle(0, 0, 2, 1);
    final Rectangle r5 = new Rectangle(0, 0, 1, 2);

    Assert.assertFalse(r0.equals(null));
    Assert.assertTrue(r0.equals(r0));
    Assert.assertFalse(r0.equals(Integer.valueOf(32)));
    Assert.assertTrue(r0.equals(r2) == false);
    Assert.assertTrue(r0.equals(r3) == false);
    Assert.assertTrue(r0.equals(r4) == false);
    Assert.assertTrue(r0.equals(r5) == false);
    Assert.assertTrue(r0.equals(r1));
  }

  @Test public void testRectangleFitExactReflexive()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 1, 1);

    Assert.assertTrue(r0.fitsInside(r0) == Fit.FIT_YES_EXACT);
  }

  @Test public void testRectangleFitExactSymmetric()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 1, 1);
    final Rectangle r1 = new Rectangle(0, 0, 1, 1);

    Assert.assertTrue(r0.fitsInside(r1) == Fit.FIT_YES_EXACT);
    Assert.assertTrue(r1.fitsInside(r0) == Fit.FIT_YES_EXACT);
  }

  @Test public void testRectangleFitLargeAntisymmetric()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 3, 3);
    final Rectangle r1 = new Rectangle(0, 0, 1, 1);

    Assert.assertTrue(r1.fitsInside(r0) == Fit.FIT_YES_WITH_EXCESS);
    Assert.assertTrue(r0.fitsInside(r1) == Fit.FIT_NO_TOO_SMALL);
  }

  @Test public void testRectangleHashCode()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 1, 1);
    final Rectangle r1 = new Rectangle(0, 0, 1, 1);

    Assert.assertTrue(r0.hashCode() == r1.hashCode());
    Assert.assertTrue(r0.hashCode() == r0.hashCode());
  }

  @Test public void testRectangleHorizontalSplit0()
    throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 1, 1);
    final Pair<Rectangle, Rectangle> p = r.splitHorizontal(0);

    Assert.assertEquals(0, p.first.x0);
    Assert.assertEquals(1, p.first.x1);
    Assert.assertEquals(0, p.first.y0);
    Assert.assertEquals(0, p.first.y1);

    Assert.assertEquals(0, p.second.x0);
    Assert.assertEquals(1, p.second.x1);
    Assert.assertEquals(1, p.second.y0);
    Assert.assertEquals(1, p.second.y1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleHorizontalSplitCannotOutOfRangeLower()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitHorizontal(-1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleHorizontalSplitCannotOutOfRangeUpper()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitHorizontal(1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleHorizontalSplitCannotTooSmall()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitHorizontal(0);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleHorizontalSplitCannotUpper()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 1, 1);
    r.splitHorizontal(1);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testRectangleInvalid0()
      throws ConstraintError
  {
    new Rectangle(0, 0, -1, 0);
  }

  @SuppressWarnings("unused") @Test(expected = ConstraintError.class) public
    void
    testRectangleInvalid1()
      throws ConstraintError
  {
    new Rectangle(0, 0, 0, -1);
  }

  @Test public void testRectangleSplitFitExact()
    throws ConstraintError
  {
    final Rectangle source = new Rectangle(0, 0, 511, 511);
    final Rectangle r = new Rectangle(0, 0, 31, 31);

    final Pair<Rectangle, Rectangle> p0 = source.splitHorizontal(31);
    Assert.assertEquals(0, p0.first.x0);
    Assert.assertEquals(511, p0.first.x1);
    Assert.assertEquals(0, p0.first.y0);
    Assert.assertEquals(31, p0.first.y1);
    Assert.assertEquals(0, p0.second.x0);
    Assert.assertEquals(511, p0.second.x1);
    Assert.assertEquals(32, p0.second.y0);
    Assert.assertEquals(511, p0.second.y1);

    final Pair<Rectangle, Rectangle> p1 = p0.first.splitVertical(31);
    Assert.assertEquals(0, p1.first.x0);
    Assert.assertEquals(31, p1.first.x1);
    Assert.assertEquals(0, p1.first.y0);
    Assert.assertEquals(31, p1.first.y1);
    Assert.assertEquals(32, p1.second.x0);
    Assert.assertEquals(511, p1.second.x1);
    Assert.assertEquals(0, p1.second.y0);
    Assert.assertEquals(31, p1.second.y1);

    Assert.assertTrue(r.fitsInside(p1.first) == Fit.FIT_YES_EXACT);
    Assert.assertTrue(p1.first.fitsInside(r) == Fit.FIT_YES_EXACT);
  }

  @Test public void testRectangleStrings()
    throws ConstraintError
  {
    final Rectangle r0 = new Rectangle(0, 0, 2, 2);
    final Rectangle r1 = new Rectangle(0, 0, 2, 2);
    final Rectangle r2 = new Rectangle(0, 0, 3, 3);

    Assert.assertEquals(r0.toString(), r1.toString());
    Assert.assertFalse(r0.toString().equals(r2.toString()));
  }

  @Test public void testRectangleVerticalSplit()
    throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 1, 1);
    final Pair<Rectangle, Rectangle> p = r.splitVertical(0);

    Assert.assertEquals(0, p.first.x0);
    Assert.assertEquals(0, p.first.x1);
    Assert.assertEquals(0, p.first.y0);
    Assert.assertEquals(1, p.first.y1);

    Assert.assertEquals(1, p.second.x0);
    Assert.assertEquals(1, p.second.x1);
    Assert.assertEquals(0, p.second.y0);
    Assert.assertEquals(1, p.second.y1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleVerticalSplitCannotOutOfRangeLower()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitVertical(-1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleVerticalSplitCannotOutOfRangeUpper()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitVertical(1);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleVerticalSplitCannotTooSmall()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 0, 0);
    r.splitVertical(0);
  }

  @Test(expected = ConstraintError.class) public
    void
    testRectangleVerticalSplitCannotUpper()
      throws ConstraintError
  {
    final Rectangle r = new Rectangle(0, 0, 1, 1);
    r.splitVertical(1);
  }
}
