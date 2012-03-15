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
import com.io7m.jrpack.PackResult.PackOK;
import com.io7m.jrpack.PackResult.PackResultCode;

public final class Viewer1D implements Runnable
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

      g.setColor(Color.WHITE);
      for (final Rectangle r : Viewer1D.this.rectangles) {
        System.out.println(r);
        g.drawRect(r.x0, r.y0, r.getWidth(), r.getHeight());
      }

      g.setColor(Color.WHITE);
      g.setXORMode(Color.BLACK);

      if (Viewer1D.this.pack_result != null) {
        switch (Viewer1D.this.pack_result) {
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
    final Viewer1D v = new Viewer1D();
    final JFrame frame = new JFrame("Viewer1D");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(v.getPanel());
    frame.pack();
    frame.setVisible(true);

    v.run();
  }

  private PackResultCode       pack_result = null;

  private Pack1D               packer;
  private final JPanel         panel;
  private ArrayList<Rectangle> rectangles;

  public Viewer1D()
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
      final JTextField input_width = new JTextField("512");
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
            final PackResult pr = Viewer1D.this.packer.insert(width);

            Viewer1D.this.pack_result = pr.type;
            switch (pr.type) {
              case PACK_RESULT_OK:
              {
                final PackOK r = (PackResult.PackOK) pr;
                Viewer1D.this.rectangles.add(r.rectangle);
                break;
              }
              case PACK_RESULT_OUT_OF_SPACE:
              {
                Viewer1D.this.pack_result =
                  PackResultCode.PACK_RESULT_OUT_OF_SPACE;
                break;
              }
              case PACK_RESULT_TOO_LARGE:
              {
                Viewer1D.this.pack_result =
                  PackResultCode.PACK_RESULT_TOO_LARGE;
                break;
              }
            }
            canvas.repaint();
          } catch (final IllegalArgumentException _) {
            // Nothing.
          } catch (final ConstraintError _) {
            // Nothing.
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
            Viewer1D.this.initializePacker();
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
        c.gridx = 3;
        c.gridy = 0;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.weightx = 1.0;
        controls_panel.add(new JPanel(), c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 0;
        c.insets = padding;
        c.gridheight = 1;
        c.gridwidth = 1;
        controls_panel.add(reset, c);
      }

      {
        final GridBagConstraints c = new GridBagConstraints();
        c.gridx = 5;
        c.gridy = 0;
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
    this.packer = new Pack1D(512, 512, 16);
  }

  @Override public void run()
  {
    // TODO Auto-generated method stub
  }
}
