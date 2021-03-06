package sos.scheduler.editor.conf.listeners;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jdom.Element;
import org.jdom.JDOMException;
import com.sos.joe.xml.Utils;
import com.sos.joe.xml.jobscheduler.SchedulerDom;

public class LocksListener {


	private    SchedulerDom        _dom        = null;

	private    Element             _config     = null;

	private    Element             _locks      = null;

	private    List                _list       = null;

	private    Element             _lock       = null;


	public LocksListener(SchedulerDom dom, Element config) throws JDOMException {
		_dom = dom;
		_config = config;
		_locks = _config.getChild("locks");

		if (_locks != null)
			_list = _locks.getChildren("lock");
	}


	private void initLocks() {
		if (_config.getChild("locks") == null) {
			_locks = new Element("locks");
			_config.addContent(_locks);
		} else {
			_locks = _config.getChild("locks");
		}
		_list = _locks.getChildren("lock");
	}


	public void fillTable(Table table) {
		table.removeAll();
		if (_list != null) {
			for (Iterator it = _list.iterator(); it.hasNext();) {
				Element e = (Element) it.next();
				TableItem item = new TableItem(table, SWT.NONE);
				String name = Utils.getAttributeValue("name", e);

				item.setText(0, name);
				if(Utils.getAttributeValue("max_non_exclusive", e).length() > 0)
					item.setText(1, "" + Utils.getIntValue("max_non_exclusive", e));
				
				if(!Utils.isElementEnabled("lock", _dom, e)) {
					item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				} 
			}
		}
	}

	public void selectLock(int index) {
		if(_list == null)
			initLocks();
		if (_list != null && index >= 0 && index < _list.size())
			_lock = (Element) _list.get(index);
		else
			_lock = null;
	}

	public Element getLock(int index) {
		selectLock(index);
		return _lock;
	}

	public String getLock() {
		String name = Utils.getAttributeValue("name", _lock);
		return name;
	}

	public boolean hasUnlimitedNonExclusiveLock() {
		return Utils.getAttributeValue("max_non_exclusive", _lock).length() == 0;
	}
	public int getMax_non_exclusive() {
		
		int m = Utils.getIntValue("max_non_exclusive", _lock);
		return m;
	}



	public void newLock() {    	
		_lock = new Element("lock");    	
	}


	public void applyLock(String name,  int maxNonExclusive, boolean unlimitedNonExclusive) {
		_dom.setChanged(true);
		String oldLockName = Utils.getAttributeValue("name", _lock); 
		_dom.setChangedForDirectory("lock", oldLockName, SchedulerDom.DELETE);
		Utils.setAttribute("name", name, _lock, _dom);
		if(unlimitedNonExclusive)
			_lock.removeAttribute("max_non_exclusive");
		else
			Utils.setAttribute("max_non_exclusive", maxNonExclusive, _lock, _dom);
		if (_list == null)
			initLocks();
		if (!_list.contains(_lock)) { 
			_list.add(_lock);

			if (_dom.isLifeElement() || _dom.isDirectory()) 
				_dom.setChangedForDirectory("lock", name, SchedulerDom.NEW);

			if (_dom.isLifeElement()) 
				_dom.getRoot().setAttribute("name", name);        		

		} else {

			if (_dom.isLifeElement())        	        
				_dom.getRoot().setAttribute("name", name);

			if (_dom.isLifeElement() || _dom.isDirectory()) 
				_dom.setChangedForDirectory("lock", name, SchedulerDom.MODIFY);

		}
		
	}


	public void removeLock(int index) {
		if (index >= 0 && index < _list.size()) {
			String name = Utils.getAttributeValue("name", (Element)_list.get(index));
			_list.remove(index);
			if (_list.size() == 0) {
				_config.removeChild("process_classes");
				_locks = null;
				_list = null;
			}
			_lock = null;
			_dom.setChanged(true);
			_dom.setChangedForDirectory("lock", name, SchedulerDom.DELETE);
		}
	}


	public boolean isValidLock(String name) {
		if (_list != null) {
			for (Iterator it = _list.iterator(); it.hasNext();) {
				Element e = (Element) it.next();
				if (Utils.getAttributeValue("name", e).equals(name))
					return false;
			}
		}
		return true;
	}


}
