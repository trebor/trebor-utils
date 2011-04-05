package org.trebor.util.encapsulate;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;

public class DefaultNodeRenderer implements INodeRenderer
{
  public static final int BORDER_SIZE = 20;
  public static final int CORNER_SIZE = BORDER_SIZE * 2;
  public static final int INSET_SIZE = 10;
  
  private static final Color FRAME_COLOR = new Color(0, 0, 255, 128);
  private static final Color SELECTED_COLOR = new Color(255, 0, 0, 128);

  @SuppressWarnings("unchecked")
  public void render(Graphics2D g, INode node)
  {
    Rectangle2D bounds = g.getClipBounds();

    Shape mShape = getShape(bounds, node);
    g.setColor(node.isSelected() ? SELECTED_COLOR : FRAME_COLOR);
    g.setStroke(new BasicStroke(BORDER_SIZE));

    if (node.isLeaf())
      g.fill(mShape);
    else
    {
      g.draw(mShape);
      {
        double width = (bounds.getWidth() - (2 * BORDER_SIZE + INSET_SIZE))
          / node.getChildCount() - (INSET_SIZE + BORDER_SIZE);
        double offset = 0;

        Enumeration<INode> children = (Enumeration<INode>)(node.children());
        while (children.hasMoreElements())
        {
          INode child = children.nextElement();

          double inset = INSET_SIZE + BORDER_SIZE;

          render((Graphics2D)g.create(
            (int)(bounds.getX() + inset + offset),
            (int)(bounds.getY() + inset),
            (int)(width + BORDER_SIZE), 
            (int)(bounds.getHeight() - 2 * inset)), child);
          
          offset += width + INSET_SIZE + BORDER_SIZE;
        }
      }
    }
  }

  public Shape getShape(Rectangle2D size, INode node)
  {
    return getShape(size.getX(), size.getY(), size.getWidth(), size.getHeight(), node);
  }
  
  public Shape getShape(double x, double y, double width, double height, INode node)
  {
    int inset = node.isLeaf() ? 0 : BORDER_SIZE / 2;
    Shape mShape = new RoundRectangle2D.Double(x + inset, y + inset,
      width - 2 * inset, height - 2 * inset, CORNER_SIZE, CORNER_SIZE);
    return mShape;
  }
}