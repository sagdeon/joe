package sos.scheduler.editor.actions.listeners;


import org.jdom.Element;
import sos.scheduler.editor.actions.ActionsDom;
import sos.scheduler.editor.actions.forms.ActionsForm;
import sos.scheduler.editor.app.Utils;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.SWT;

public class ActionListener {
   
	private ActionsDom           _dom                 = null;

    private Element              _action             = null;
    
    private ActionsForm gui                 = null;

   
    public ActionListener(ActionsDom dom, Element action, ActionsForm _gui) {
        _dom = dom;
        _action = action;
        gui = _gui;
    }


	public String getName() {
		return Utils.getAttributeValue("name", _action);
	}


	public void setName(String name) {
		Utils.setAttribute("name", name, _action);
		gui.updateAction(name);
		_dom.setChanged(true);
	}
   
   

}