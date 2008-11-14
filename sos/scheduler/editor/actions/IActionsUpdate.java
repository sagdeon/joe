package sos.scheduler.editor.actions;

import sos.scheduler.editor.app.IDataChanged;

public interface IActionsUpdate extends IDataChanged {
	 
	public void updateActions();
	
	public void updateAction(String name);
	
	public void updateCommands();
	
	public void updateCommand();
	
	public void updateTreeItem(String s);

}
 