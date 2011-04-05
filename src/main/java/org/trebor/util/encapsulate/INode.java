package org.trebor.util.encapsulate;

//import java.io.File;

import javax.swing.tree.MutableTreeNode;

interface INode extends MutableTreeNode
{
  /**
   * The descriptive label for this node.
   * 
   * @return the descriptive label for this node.
   */
  
  public String getLabel();
  
  /**
   * Test if this is a selected {@link INode}.
   * 
   * return true if this is a selected node.
   */
  
  public boolean isSelected();
  
  /**
   * Set this node as selected.
   * 
   * @param selected true if this is a selected node
   */
  
  public void setSelected(boolean selected);
  
  /** {@inheritDoc} */
  
  @Override
  public INode getParent();
  
  /** {@inheritDoc} */
  
  @Override
  public INode getChildAt(int index);
  
  public INode getNextSib();
  
  public INode getPreviousSib();
}