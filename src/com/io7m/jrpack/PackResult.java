package com.io7m.jrpack;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * Pseudo algebraic data type representing the result of packing.
 */

public abstract class PackResult
{
  public static final class PackOK extends PackResult
  {
    public final @Nonnull Rectangle rectangle;

    @SuppressWarnings("synthetic-access") public PackOK(
      final @Nonnull Rectangle rectangle)
      throws ConstraintError
    {
      super(PackResultCode.PACK_RESULT_OK);
      this.rectangle = Constraints.constrainNotNull(rectangle, "rectangle");
    }
  }

  public static final class PackOutOfSpace extends PackResult
  {
    @SuppressWarnings("synthetic-access") public PackOutOfSpace()
      throws ConstraintError
    {
      super(PackResultCode.PACK_RESULT_OUT_OF_SPACE);
    }
  }

  public static enum PackResultCode
  {
    PACK_RESULT_OK,
    PACK_RESULT_OUT_OF_SPACE,
    PACK_RESULT_TOO_LARGE
  }

  public static final class PackTooLarge extends PackResult
  {
    @SuppressWarnings("synthetic-access") public PackTooLarge()
      throws ConstraintError
    {
      super(PackResultCode.PACK_RESULT_TOO_LARGE);
    }
  }

  public final @Nonnull PackResult.PackResultCode type;

  private PackResult(
    final @Nonnull PackResult.PackResultCode type)
    throws ConstraintError
  {
    this.type = Constraints.constrainNotNull(type, "type");
  }
}
