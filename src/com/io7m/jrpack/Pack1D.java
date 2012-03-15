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

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * 1D span packing.
 */

public final class Pack1D
{
  private final int       element_height;
  private final Rectangle bounds;
  private int             current_x;
  private int             current_y;

  public Pack1D(
    final int width,
    final int height,
    final int element_height)
    throws ConstraintError
  {
    Constraints.constrainRange(width, 1, Integer.MAX_VALUE, "Width");
    Constraints.constrainRange(height, 1, Integer.MAX_VALUE, "Height");

    this.current_x = 0;
    this.current_y = 0;
    this.element_height =
      Constraints.constrainRange(element_height, 1, height, "Element height");
    this.bounds = new Rectangle(0, 0, width - 1, height - 1);
  }

  /**
   * Return the height of the current rectangle.
   * 
   * @return The height of the current rectangle.
   */

  public int getHeight()
  {
    return this.bounds.getHeight();
  }

  /**
   * Return the uppermost valid X coordinate in the current rectangle.
   * 
   * @return The uppermost valid X coordinate in the current rectangle.
   */

  public int getUpperX()
  {
    return this.bounds.getUpperX();
  }

  /**
   * Return the uppermost valid Y coordinate in the current rectangle.
   * 
   * @return The uppermost valid Y coordinate in the current rectangle.
   */

  public int getUpperY()
  {
    return this.bounds.getUpperY();
  }

  /**
   * Return the width of the current rectangle.
   * 
   * @return The width of the current rectangle.
   */

  public int getWidth()
  {
    return this.bounds.getWidth();
  }

  /**
   * Pack an element of the given width into the current bounds.
   * 
   * @param width
   *          The element width.
   * @return <ul>
   *         <li>PackOK iff the given element was packed correctly.</li>
   *         <li>PackOutOfSpace iff the given element would fit within a
   *         rectangle of this size but no space remained to do it.</li>
   *         <li>PackTooLarge iff the given element would be too large to fit
   *         even if the current rectangle was empty.</li>
   *         </ul>
   * @throws ConstraintError
   */

  public PackResult insert(
    final int width)
    throws ConstraintError
  {
    if (width > this.bounds.getWidth()) {
      return new PackResult.PackTooLarge();
    }
    if (((this.current_x + width) - 1) <= this.bounds.getUpperX()) {
      return new PackResult.PackOK(this.pack(width));
    }

    final int new_x = 0;
    final int new_y = this.current_y + this.element_height;

    if (((new_y + this.element_height) - 1) <= this.bounds.getUpperY()) {
      this.current_x = new_x;
      this.current_y = new_y;
      return new PackResult.PackOK(this.pack(width));
    }

    return new PackResult.PackOutOfSpace();
  }

  private Rectangle pack(
    final int width)
    throws ConstraintError
  {
    final int x0 = this.current_x;
    final int x1 = (this.current_x + width) - 1;
    final int y0 = this.current_y;
    final int y1 = (this.current_y + this.element_height) - 1;

    final Rectangle r = new Rectangle(x0, y0, x1, y1);
    this.current_x = x1;

    return r;
  }
}
