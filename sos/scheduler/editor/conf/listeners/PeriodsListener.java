package sos.scheduler.editor.conf.listeners;

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.jdom.Element;

import sos.scheduler.editor.app.Utils;
import sos.scheduler.editor.conf.SchedulerDom;

public class PeriodsListener {

    private SchedulerDom _dom;

    private Element      _parent;

    private List         _list;

    private int          _period;


    public PeriodsListener(SchedulerDom dom, Element parent) {
        _dom = dom;
        _parent = parent;
        _list = parent.getChildren("period");
    }


    public boolean isOnOrder() {
        Element job = _parent;
        while (!job.getName().equals("job") && !job.getName().equals("add_order"))
            job = job.getParentElement();

        return Utils.isAttributeValue("order", job) && job.getName().equals("job") || Utils.isAttributeValue("id", job)
                && job.getName().equals("add_order");
    }


    public void fillTable(Table table) {
        table.removeAll();

        if (_list != null) {
            for (Iterator it = _list.iterator(); it.hasNext();) {
                Element e = (Element) it.next();
                TableItem item = new TableItem(table, SWT.NONE);

                item.setText(0, Utils.isAttributeValue("let_run", e) ? "Yes" : "No");
                item.setText(1, Utils.getAttributeValue("begin", e));
                item.setText(2, Utils.getAttributeValue("end", e));
                item.setText(3, Utils.getAttributeValue("repeat", e));
                item.setText(4, Utils.getAttributeValue("single_start", e));
            }
        }
    }   

    public void removePeriod(int index) {
        if (index >= 0 && index < _list.size()) {
            _list.remove(index);
            _period = -1;
            _dom.setChanged(true);
        } else {
            System.out.println("Bad period index for removal!");
        }
    }


    public Element getNewPeriod() {
        _period = -1;
        return new Element("period");
    }


    public Element getPeriod(int index) {
        if (index >= 0 && index < _list.size()) {
            _period = index;
            return (Element) ((Element) _list.get(index)).clone();
        } else {
            System.out.println("Bad period index for selection!");
            return null;
        }
    }


    public void applyPeriod(Element period) {
        if (_period == -1)
            _list.add(period);
        else
            _list.set(_period, period);
        _dom.setChanged(true);
    }


	public List get_list() {
		return _list;
	}
}
