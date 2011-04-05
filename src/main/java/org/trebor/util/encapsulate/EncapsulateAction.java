package org.trebor.util.encapsulate;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.KeyStroke;

/**
 * EncapsualteAction is derived from AbstractAction and provides a standard
 * class from which to subclass Encapsulate actions.
 */

public abstract class EncapsulateAction extends AbstractAction
{
  public static final int NO_ACCELERATOR = -1;
  
  private static final long serialVersionUID = -7711959561886492091L;

  KeyStroke mAccelerator;

  /**
   * Create an action with a given name, shortcut key and description.
   * This version assumes no accelerator key.
   * 
   * @param name name of this action
   * @param description description of this action
   */

  public EncapsulateAction(String name, String description)
  {
    this(name, description, NO_ACCELERATOR);
  }

  /**
   * Create an action with a given name, shortcut key and description.
   * This version assumes no key modifiers for shortcut.
   * 
   * @param name name of this action
   * @param description description of this action
   * @param keyCode identifies key for this action
   */

  public EncapsulateAction(String name, String description, int keyCode)
  {
    this(name, description, keyCode, 0);
  }

  /**
   * Create an action with a given name, shortcut key and description.
   * 
   * @param name name of this action
   * @param description description of this action
   * @param keyCode identifies shortcut key for this action
   * @param modifiers modifiers for shortcut (SHIFT, META, etc.)
   */

  public EncapsulateAction(String name, String description, int keyCode,
    int... modifiers)
  {
    int allModifiers = 0;
    for (int modifier: modifiers)
      allModifiers |= modifier;
    
    putValue(NAME, name);
    putValue(SHORT_DESCRIPTION, description);
    if (keyCode != NO_ACCELERATOR)
      putValue(ACCELERATOR_KEY, mAccelerator = KeyStroke.getKeyStroke(keyCode,
        allModifiers));
    setEnabled(true);
  }

  /**
   * Get text name of the accelerator key for this action.
   * 
   * @return some random description of the accelerator key
   */

  public String getAcceleratorDescription()
  {
    // if there is no accelerator indicate this

    if (mAccelerator == null)
      return "<no key>";

    // KeyEvent.getKeyText(accelerator.getKeyCode());
    // KeyEvent.getKeyModifiersText(accelerator.getModifiers());
    //
    // this is NOT being used because it sometimes returns single
    // character uni-code glitz-y-ness which doesn't always do the
    // right thing

    // get and clean up the key name

    String key = mAccelerator.toString();
    key = key.replaceAll("pressed", "").trim();
    key = key.replaceAll("  ", " ");
    key = key.replaceAll(" ", "-");
    return key;
  }

  /**
   * Called when the given action is to be executed. This function must be
   * implemented by the subclass.
   * 
   * @param e action event
   */

  abstract public void actionPerformed(ActionEvent e);

  public KeyStroke getAccelerator()
  {
    return mAccelerator;
  }
}
