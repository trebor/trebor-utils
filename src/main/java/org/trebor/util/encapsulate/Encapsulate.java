package org.trebor.util.encapsulate;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.event.ActionEvent;

import static java.awt.event.KeyEvent.*;

class Encapsulate extends JFrame
{
  private static final long serialVersionUID = -9105388521416928323L;
  private INode mTree;
  private INodeRenderer mNodeRenderer;

  
  @SuppressWarnings("serial")
  EncapsulateAction[] mActions = 
  {
    new EncapsulateAction("exit", "exit the program", VK_ESCAPE)
    {
      public void actionPerformed(ActionEvent e)
      {
        System.exit(0);
      }
    },
    
    new EncapsulateAction("next", "move to next sibling", VK_F,
      CTRL_DOWN_MASK)
    {
      public void actionPerformed(ActionEvent e)
      {
        nextSibling();
      }
    },
    
    new EncapsulateAction("previous", "move to previous sibling", VK_B,
      CTRL_DOWN_MASK)
    {
      public void actionPerformed(ActionEvent e)
      {
        previousSibling();
      }
    },
    
    new EncapsulateAction("up", "move to parent", VK_P,
      CTRL_DOWN_MASK)
    {
      public void actionPerformed(ActionEvent e)
      {
        up();
      }
    },
    
    new EncapsulateAction("down", "move down to children", VK_N,
      CTRL_DOWN_MASK)
    {
      public void actionPerformed(ActionEvent e)
      {
        down();
      }
    },
    
    new EncapsulateAction("add", "add a child", VK_A)
    {
      public void actionPerformed(ActionEvent e)
      {
        add();
      }
    },
    
    new EncapsulateAction("remove", "remove this node", VK_D)
    {
      public void actionPerformed(ActionEvent e)
      {
        remove();
      }
    },
  };

  private INode mCurrentNode;
  private INodeFactory mNodeFactory;
  
  
  public static void main(String[] args)
  {
    new Encapsulate(new DefaultNodeRenderer(), new DefaultNodeFactory());
  }

  public Encapsulate(INodeRenderer nodeRenderer, INodeFactory nodeFactory)
  {
    mNodeRenderer = nodeRenderer;
    mNodeFactory = nodeFactory;
    mTree = mNodeFactory.createNode(null);
    mNodeFactory.createNode(mTree);
    mNodeFactory.createNode(mTree);
    mCurrentNode = mTree;
    mCurrentNode.setSelected(true);
    
    // construct the frame

    constructFrame(getContentPane());
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // get the graphics device from the local graphic environment

    GraphicsDevice gv = GraphicsEnvironment.
      getLocalGraphicsEnvironment().getScreenDevices()[0];

    // if full screen is supported setup frame accordingly

    if (!gv.isFullScreenSupported())
    {
      setUndecorated(true);
      setVisible(true);
      pack();
      gv.setFullScreenWindow(this);
    }
    // otherwise just make a big frame

    else
    {
      pack();
      setExtendedState(MAXIMIZED_BOTH);
      setVisible(true);
    }
  }

  // construct the frame

  public void constructFrame(Container frame)
  {
    // create the paint area

    JPanel paintArea = new JPanel()
    {
      private static final long serialVersionUID = 490573247781954862L;

      public void paint(Graphics graphics)
      {
        Graphics2D g = (Graphics2D)graphics;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

        mNodeRenderer.render(g, mTree);
      }
    };

    // add paint area to the frame

    frame.add(paintArea);

    // add actions to the root pain

    for (EncapsulateAction action : mActions)
    {
      getRootPane().getActionMap().put(action.getValue(Action.NAME), action);
      getRootPane().getInputMap().put(action.getAccelerator(), action.getValue(Action.NAME));
    }
  }

  public void nextSibling()
  {
    setCurrentNode(mCurrentNode.getNextSib());
  }
  
  public void previousSibling()
  {
    setCurrentNode(mCurrentNode.getPreviousSib());
  }
  
  public void up()
  {
    setCurrentNode(mCurrentNode.getParent());
  }
  
  public void down()
  {
    if (!mCurrentNode.isLeaf())
      setCurrentNode(mCurrentNode.getChildAt(0));
  }
  
  public void add()
  {
    mNodeFactory.createNode(mCurrentNode);
    repaint();
  }
  
  public void remove()
  {
    INode parent = mCurrentNode.getParent();
    if (null != parent)
    {
      parent.remove(mCurrentNode);
      setCurrentNode(parent);
    }
  }
  
  public INode setCurrentNode(INode node)
  {
    INode old = null;
    
    if (null != node)
    {
      old = mCurrentNode;
      mCurrentNode.setSelected(false);
      mCurrentNode = node;
      mCurrentNode.setSelected(true);
      repaint();
    }
    
    return old;
  }
}
