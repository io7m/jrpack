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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.functional.Pair;
import com.io7m.jaux.functional.Procedure;
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackResultCode;

public final class Viewer2D implements Runnable
{
  private class PackCanvas extends Canvas
  {
    private static final long serialVersionUID = 474729240519661207L;

    public PackCanvas()
    {
      // Nothing.
    }

    @SuppressWarnings("synthetic-access") @Override public void paint(
      final Graphics g)
    {
      g.setColor(Color.BLUE);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());

      try {
        Viewer2D.this.packer
          .iterate(new Procedure<Pair<Integer, Rectangle>>() {
            @Override public void call(
              final Pair<Integer, Rectangle> pair)
            {
              final double depth = pair.first.intValue();
              final double max = Viewer2D.this.packer.getMaximumDepth();
              final double ratio = depth / max;
              g.setColor(new Color(1.0f, 1.0f, 1.0f, (float) ratio));
              g.fillRect(
                pair.second.x0,
                pair.second.y0,
                pair.second.getWidth(),
                pair.second.getHeight());
              g.drawRect(
                pair.second.x0,
                pair.second.y0,
                pair.second.getWidth(),
                pair.second.getHeight());
            }
          });
      } catch (final ConstraintError e) {
        e.printStackTrace();
      }

      g.setColor(Color.WHITE);
      g.setXORMode(Color.BLACK);

      if (Viewer2D.this.pack_result != null) {
        switch (Viewer2D.this.pack_result) {
          case PACK_RESULT_OK:
            g.drawString("OK", 4, 16);
            break;
          case PACK_RESULT_OUT_OF_SPACE:
            g.drawString("Out of space", 4, 16);
            break;
          case PACK_RESULT_TOO_LARGE:
            g.drawString("Too large", 4, 16);
            break;
        }
      }
    }
  }

  public static void main(
    final String args[])
    throws ConstraintError
  {
    final Viewer2D v = new Viewer2D();
    final JFrame frame = new JFrame("Viewer2D");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(v.getPanel());
    frame.pack();
    frame.setVisible(true);

    v.run();
  }

  private Pack2D               packer;
  private final JPanel         panel;
  private ArrayList<Rectangle> rectangles;
  private PackResultCode       pack_result = null;

  public Viewer2D()
    throws ConstraintError
  {
    this.initializePacker();

    this.panel = new JPanel();
    this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
    // this.panel.setBorder(BorderFactory.createLineBorder(Color.RED));

    {
      final JPanel canvas_panel = new JPanel();
      final PackCanvas canvas = new PackCanvas();
      canvas.setSize(512, 512);
      canvas.setMinimumSize(new Dimension(512, 512));
      canvas_panel.add(canvas);
      this.panel.add(canvas_panel);

      final JLabel label_width = new JLabel("Width");
      final JLabel label_height = new JLabel("Height");
      final JTextField input_width = new JTextField("512");
      final JTextField input_height = new JTextField("512");
      final JButton insert = new JButton("Insert");
      final JButton reset = new JButton("Reset");
      final JButton quit = new JButton("Quit");

      insert.addActionListener(new ActionListener() {
        @SuppressWarnings("synthetic-access") @Override public
          void
          actionPerformed(
            final ActionEvent e)
        {
          try {
            final int width = Integer.parseInt(input_width.getText());
            final int height = Integer.parseInt(input_height.getText());

            final PackResult pr =
              Viewer2D.this.packer.insert(new Rectangle(
                0,
                0,
                width - 1,
                height - 1));
            switch (pr.type) {
              case PACK_RESULT_OK:
                final PackOK ok = (PackOK) pr;
                Viewer2D.this.rectangles.add(ok.rectangle);
                Viewer2D.this.pack_result = pr.type;
                break;
              case PACK_RESULT_OUT_OF_SPACE:
                Viewer2D.this.pack_result = pr.type;
                break;
              case PACK_RESULT_TOO_LARGE:
                Viewer2D.this.pack_result = pr.type;
                break;
            }

            canvas.repaint();
          } catch (final IllegalArgumentException _) {
            // Ignore
          } catch (final ConstraintError _) {
            // Ignore
          }
        }
      });

      reset.addActionListener(new ActionListener() {
        @SuppressWarnings("synthetic-access") @Override public
          void
          actionPerformed(
            final ActionEvent e)
        {
          try {
            Viewer2D.this.initializePacker();
            canvas.repaint();
          } catch (final ConstraintError e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
      });

      quit.addActionListener(new ActionListener() {
        @Override public void actionPerformed(
          final ActionEvent event)
        {
          System.exit(0);
        }
      });

      final JPanel controls_panel = new JPanel();
      controls_panel.setLayout(new GridBagLayout());

      final Insets padding = new Insets(3, 3, 3, 3);

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(label_width, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(input_width, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 2;
        c.gridy = 0;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(insert, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(label_height, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(input_height, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 1;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        controls_panel.add(new JPanel(), c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 1;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(reset, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 5;
        c.gridy = 1;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(quit, c);
      }

      this.panel.add(controls_panel);
    }
  }

  private Component getPanel()
  {
    return this.panel;
  }

  private void initializePacker()
    throws ConstraintError
  {
    this.rectangles = new ArrayList<Rectangle>();
    this.packer = new Pack2D(new Rectangle(0, 0, 511, 511));
  }

  @Override public void run()
  {
    // TODO Auto-generated method stub
  }
}
