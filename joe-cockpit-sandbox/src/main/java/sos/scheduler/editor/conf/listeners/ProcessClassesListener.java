package sos.scheduler.editor.conf.listeners;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.sos.joe.xml.Utils;
import com.sos.joe.xml.jobscheduler.SchedulerDom;

import com.sos.scheduler.model.objects.JSObjProcessClass;

public class ProcessClassesListener {
	@SuppressWarnings("unused") private final String conClassName = this.getClass().getSimpleName();
	@SuppressWarnings("unused") private static final String conSVNVersion = "$Id$";
	@SuppressWarnings("unused") private final Logger logger = Logger.getLogger(this.getClass());
	

	private final static String	CATCHALL		= "<empty>";
	private SchedulerDom		_dom			= null;
	private Element				_config			= null;
	private Element				_processClasses	= null;
	private List				_list			= null;
	private Element				_class			= null;
	private JSObjProcessClass objJSProcessClass = null;
	
	public ProcessClassesListener (final JSObjProcessClass pobjJSProcessClass) {
		objJSProcessClass = pobjJSProcessClass;
		
	}
	public ProcessClassesListener(final SchedulerDom dom, final Element config) throws JDOMException {
		_dom = dom;
		_config = config;
		_processClasses = _config.getChild("process_classes");
		if (_processClasses != null)
			_list = _processClasses.getChildren("process_class");
	}

	private void initClasses() {
		if (_config.getChild("process_classes") == null) {
			_processClasses = new Element("process_classes");
			_config.addContent(_processClasses);
		}
		else {
			_processClasses = _config.getChild("process_classes");
		}
		_list = _processClasses.getChildren("process_class");
	}

	public void fillTable(final Table table) {
		table.removeAll();
		if (_list != null) {
			for (Iterator it = _list.iterator(); it.hasNext();) {
				Element e = (Element) it.next();
				TableItem item = new TableItem(table, SWT.NONE);
				String name = Utils.getAttributeValue("name", e);
				if (name.equals(""))
					name = CATCHALL;
				item.setText(0, name);
				item.setText(1, "" + Utils.getIntValue("max_processes", e));
				item.setText(2, Utils.getAttributeValue("remote_scheduler", e));
				if (!Utils.isElementEnabled("process_class", _dom, e)) {
					item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				}
			}
		}
	}

	public void selectProcessClass(final int index) {
		if (_list == null)
			initClasses();
		if (_list != null && index >= 0 && index < _list.size())
			_class = (Element) _list.get(index);
		else
			_class = null;
	}

	public Element getProcessElement(final int index) {
		selectProcessClass(index);
		return _class;
	}

	public String getProcessClass() {
		String name = Utils.getAttributeValue("name", _class);
		if (name.equals(CATCHALL))
			name = "";
		return name;
	}

	public String getRemoteHost() {
		String host = Utils.getAttributeValue("remote_scheduler", _class);
		try {
			host = host.substring(0, host.indexOf(":"));
		}
		catch (Exception e) {
			host = "";
		}
		return host.trim();
	}

	public String getRemotePort() {
		String port = Utils.getAttributeValue("remote_scheduler", _class);
		try {
			port = port.substring(port.indexOf(":") + 1);
		}
		catch (Exception e) {
			port = "";
		}
		return port.trim();
	}

	public String getMaxProcesses() {
		return Utils.getAttributeValue("max_processes", _class);
	}

	public String getSpoolerID() {
		return Utils.getAttributeValue("spooler_id", _class);
	}

	public void setIgnoreProcessClasses(final boolean ignore) {
		if (_processClasses == null) {
			Element config = _dom.getRoot().getChild("config");
			_processClasses = config.getChild("process_classes");
			if (_processClasses == null) {
				_processClasses = new Element("process_classes");
				config.addContent(_processClasses);
			}
		}
		Utils.setAttribute("ignore", ignore, false, _processClasses, _dom);
	}

	public boolean isIgnoreProcessClasses() {
		if (_processClasses != null)
			return Utils.getAttributeValue("ignore", _processClasses).equals("yes") ? true : false;
		else
			return false;
	}

	public boolean isReplace() {
		//default ist true daher auch gleich leerstring
		return Utils.getAttributeValue("replace", _class).equals("") || Utils.getBooleanValue("replace", _class);
	}

	public void newProcessClass() {
		_class = new Element("process_class");
	}

	public void applyProcessClass(final String processClass, final String host, final String port, final int maxProcesses) {
		_dom.setChanged(true);
		_dom.setChangedForDirectory("process_class", Utils.getAttributeValue("name", _class), SchedulerDom.DELETE);
		Utils.setAttribute("name", processClass, _class, _dom);
		Utils.setAttribute("max_processes", maxProcesses, _class, _dom);
		if (host.trim().concat(port.trim()).length() > 0) {
			Utils.setAttribute("remote_scheduler", host.trim() + ":" + port.trim(), _class, _dom);
		}
		if (_list == null)
			initClasses();
		if (!_list.contains(_class)) {
			_list.add(_class);
			_dom.setChangedForDirectory("process_class", processClass, SchedulerDom.NEW);
		}
		else
			if (_dom.isLifeElement()) {
				_dom.setChangedForDirectory("process_class", processClass, SchedulerDom.NEW);
				_dom.getRoot().setAttribute("name", _class.getAttributeValue("name"));
			}
			else {
				_dom.setChangedForDirectory("process_class", processClass, SchedulerDom.MODIFY);
			}
	}

	public void removeProcessClass(final int index) {
		if (index >= 0 && index < _list.size()) {
			String processClass = Utils.getAttributeValue("name", (Element) _list.get(index));
			_list.remove(index);
			if (_list.size() == 0 && !isIgnoreProcessClasses()) {
				_config.removeChild("process_classes");
				_processClasses = null;
				_list = null;
			}
			_class = null;
			_dom.setChanged(true);
			_dom.setChangedForDirectory("process_class", processClass, SchedulerDom.DELETE);
		}
	}

	public boolean isValidClass(final String name) {
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
