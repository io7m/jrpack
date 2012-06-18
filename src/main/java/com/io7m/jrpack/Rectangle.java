package com.io7m.jrpack;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;

/**
 * An immutable rectangle class with integer coordinates, defining inclusive
 * bounds.
 */

public final class Rectangle
{
  /**
   * Type denoting how a rectangle fits into another rectangle.
   */

  static enum Fit
  {
    FIT_NO_TOO_SMALL,
    FIT_YES_EXACT,
    FIT_YES_WITH_EXCESS
  }

  public final int x0;
  public final int y0;
  public final int x1;

  public final int y1;

  public Rectangle(
    final int x0,
    final int y0,
    final int x1,
    final int y1)
    throws ConstraintError
  {
    this.x0 = Constraints.constrainLessThan(x0, x1 + 1);
    this.y0 = Constraints.constrainLessThan(y0, y1 + 1);
    this.x1 = x1;
    this.y1 = y1;
  }

  @Override public boolean equals(
    final Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final Rectangle other = (Rectangle) obj;
    if (this.x0 != other.x0) {
      return false;
    }
    if (this.x1 != other.x1) {
      return false;
    }
    if (this.y0 != other.y0) {
      return false;
    }
    if (this.y1 != other.y1) {
      return false;
    }
    return true;
  }

  /**
   * Return the manner in which this rectangle fits inside the rectangle
   * referenced by <code>other</code>.
   * 
   * @param other
   *          The other rectangle.
   */

  public Fit fitsInside(
    final Rectangle other)
  {
    final int width_this = this.getWidth();
    final int width_other = other.getWidth();
    final int height_this = this.getHeight();
    final int height_other = other.getHeight();

    if ((width_this == width_other) && (height_this == height_other)) {
      return Fit.FIT_YES_EXACT;
    }
    if ((width_this <= width_other) && (height_this <= height_other)) {
      return Fit.FIT_YES_WITH_EXCESS;
    }
    return Fit.FIT_NO_TOO_SMALL;
  }

  /**
   * Return the height of the rectangle.
   */

  public int getHeight()
  {
    return (this.y1 - this.y0) + 1;
  }

  /**
   * Return the uppermost valid X value of the rectangle.
   */

  public int getUpperX()
  {
    return this.x1;
  }

  /**
   * Return the uppermost valid Y value of the rectangle.
   */

  public int getUpperY()
  {
    return this.y1;
  }

  /**
   * Return the width of the rectangle.
   */

  public int getWidth()
  {
    return (this.x1 - this.x0) + 1;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.x0;
    result = (prime * result) + this.x1;
    result = (prime * result) + this.y0;
    result = (prime * result) + this.y1;
    return result;
  }

  /**
   * Split the given rectangle horizontally at <code>at_y</code>.
   * 
   * @return A pair of rectangles.
   * @throws ConstraintError
   *           Iff the rectangle was too small to split.
   */

  public Pair<Rectangle, Rectangle> splitHorizontal(
    final int at_y)
    throws ConstraintError
  {
    final int r0_x0 = this.x0;
    final int r0_y0 = this.y0;
    final int r0_x1 = this.x1;
    final int r0_y1 = this.y0 + at_y;

    final int r1_x0 = this.x0;
    final int r1_y0 = this.y0 + at_y + 1;
    final int r1_x1 = this.x1;
    final int r1_y1 = this.y1;

    final Rectangle r0 = new Rectangle(r0_x0, r0_y0, r0_x1, r0_y1);
    final Rectangle r1 = new Rectangle(r1_x0, r1_y0, r1_x1, r1_y1);

    return new Pair<Rectangle, Rectangle>(r0, r1);
  }

  /**
   * Split the given rectangle vertically at <code>at_x</code>.
   * 
   * @return A pair of rectangles.
   * @throws ConstraintError
   *           Iff the rectangle was too small to split.
   */

  public Pair<Rectangle, Rectangle> splitVertical(
    final int at_x)
    throws ConstraintError
  {
    final int r0_x0 = this.x0;
    final int r0_y0 = this.y0;
    final int r0_x1 = this.x0 + at_x;
    final int r0_y1 = this.y1;

    final int r1_x0 = this.x0 + at_x + 1;
    final int r1_y0 = this.y0;
    final int r1_x1 = this.x1;
    final int r1_y1 = this.y1;

    final Rectangle r0 = new Rectangle(r0_x0, r0_y0, r0_x1, r0_y1);
    final Rectangle r1 = new Rectangle(r1_x0, r1_y0, r1_x1, r1_y1);

    return new Pair<Rectangle, Rectangle>(r0, r1);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[Rectangle (");
    builder.append(this.x0);
    builder.append(",");
    builder.append(this.y0);
    builder.append(") (");
    builder.append(this.x1);
    builder.append(",");
    builder.append(this.y1);
    builder.append(")]");
    return builder.toString();
  }
}
