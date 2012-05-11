package com.io7m.jrpack;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jaux.functional.Procedure;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackOutOfSpace;
import com.io7m.jrpack.PackResult.PackTooLarge;

/**
 * 2D rectangle packing.
 */

public final class Pack2D
{
  private static final class Node
  {
    public @CheckForNull Node           left;
    public @CheckForNull Node           right;
    public final @Nonnull Rectangle     rectangle;
    public boolean                      occupied;
    public final @Nonnull AtomicInteger tree_depth;

    public Node(
      final @Nonnull Rectangle rectangle,
      final @Nonnull AtomicInteger tree_depth)
    {
      this.rectangle = rectangle;
      this.occupied = false;
      this.tree_depth = tree_depth;
    }

    public @Nonnull Node insert(
      final @Nonnull Rectangle input)
      throws ConstraintError
    {
      assert (input != null);
      return this.insertInner(input);
    }

    private Node insertInner(
      final @Nonnull Rectangle input)
      throws ConstraintError
    {
      if (this.isLeaf()) {
        if (this.occupied) {
          return null;
        }

        switch (input.fitsInside(this.rectangle)) {
          case FIT_NO_TOO_SMALL:
            return null;
          case FIT_YES_EXACT:
            this.occupied = true;
            return this;
          case FIT_YES_WITH_EXCESS:
          {
            final int width_diff =
              this.rectangle.getWidth() - input.getWidth();
            final int height_diff =
              this.rectangle.getHeight() - input.getHeight();

            Pair<Rectangle, Rectangle> rectangles;

            if (width_diff > height_diff) {
              final int split = input.getWidth() - 1;
              rectangles = this.rectangle.splitVertical(split);
            } else {
              final int split = input.getHeight() - 1;
              rectangles = this.rectangle.splitHorizontal(split);
            }

            this.left = new Node(rectangles.first, this.tree_depth);
            this.right = new Node(rectangles.second, this.tree_depth);
            this.tree_depth.incrementAndGet();
            return this.left.insertInner(input);
          }
        }
      }

      assert this.left != null;
      final Node new_node = this.left.insertInner(input);
      if (new_node != null) {
        return new_node;
      }

      assert this.right != null;
      return this.right.insertInner(input);
    }

    boolean isLeaf()
    {
      return (this.left == null) && (this.right == null);
    }
  }

  private final @Nonnull Node          root;
  private final @Nonnull AtomicInteger maximum_depth;

  public Pack2D(
    final int width,
    final int height)
    throws ConstraintError
  {
    this(new Rectangle(0, 0, width - 1, height - 1));
  }

  public Pack2D(
    final @Nonnull Rectangle initial)
    throws ConstraintError
  {
    this.maximum_depth = new AtomicInteger(0);
    this.root =
      new Node(
        Constraints.constrainNotNull(initial, "Initial rectangle"),
        this.maximum_depth);
  }

  /**
   * Return the height of the current rectangle.
   * 
   * @return The height of the current rectangle.
   */

  public int getHeight()
  {
    return this.root.rectangle.getHeight();
  }

  /**
   * Return the current maximum depth of the tree.
   * 
   * @return The depth of the tree.
   */

  public int getMaximumDepth()
  {
    return this.maximum_depth.get();
  }

  /**
   * Return the uppermost valid X coordinate in the current rectangle.
   * 
   * @return The uppermost valid X coordinate in the current rectangle.
   */

  public int getUpperX()
  {
    return this.root.rectangle.getUpperX();
  }

  /**
   * Return the uppermost valid Y coordinate in the current rectangle.
   * 
   * @return The uppermost valid Y coordinate in the current rectangle.
   */

  public int getUpperY()
  {
    return this.root.rectangle.getUpperY();
  }

  /**
   * Return the width of the current rectangle.
   * 
   * @return The width of the current rectangle.
   */

  public int getWidth()
  {
    return this.root.rectangle.getWidth();
  }

  /**
   * Pack a given rectangle into the available space.
   * 
   * @param input
   *          The input rectangle.
   * @return <ul>
   *         <li>PackOK iff the given element was packed correctly, with a
   *         rectangle representing the resulting coordinates of the rectangle
   *         within the space.</li>
   *         <li>PackOutOfSpace iff the given element would fit within a
   *         rectangle of this size but no space remained to do it.</li>
   *         <li>PackTooLarge iff the given element would be too large to fit
   *         even if the current rectangle was empty.</li>
   *         </ul>
   * @throws ConstraintError
   *           Iff <code>input == null</code>.
   */

  public @Nonnull PackResult insert(
    final @Nonnull Rectangle input)
    throws ConstraintError
  {
    Constraints.constrainNotNull(input, "Input rectangle");

    if (input.getWidth() > this.root.rectangle.getWidth()) {
      return new PackTooLarge();
    }
    if (input.getHeight() > this.root.rectangle.getHeight()) {
      return new PackTooLarge();
    }

    final Node node = this.root.insert(input);
    if (node != null) {
      return new PackOK(node.rectangle);
    }

    return new PackOutOfSpace();
  }

  /**
   * Apply the function <code>f</code> to each rectangle contained in the
   * tree. The function <code>f</code> is passed the current rectangle and the
   * depth in the tree.
   * 
   * @param f
   *          The function to apply.
   * @throws ConstraintError
   *           Iff <code>f == null</code>.
   */

  public void iterate(
    final @Nonnull Procedure<Pair<Integer, Rectangle>> f)
    throws ConstraintError
  {
    Constraints.constrainNotNull(f, "Function");
    this.iterateInner(f, this.root, Integer.valueOf(0));
  }

  private void iterateInner(
    final @Nonnull Procedure<Pair<Integer, Rectangle>> f,
    final @Nonnull Node node,
    final @Nonnull Integer depth)
  {
    f.call(new Pair<Integer, Rectangle>(depth, node.rectangle));

    if (node.isLeaf() == false) {
      this.iterateInner(f, node.left, Integer.valueOf(depth.intValue() + 1));
      this.iterateInner(f, node.right, Integer.valueOf(depth.intValue() + 1));
    }
  }
}
