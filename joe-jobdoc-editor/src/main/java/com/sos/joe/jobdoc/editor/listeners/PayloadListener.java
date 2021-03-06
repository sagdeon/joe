package com.sos.joe.jobdoc.editor.listeners;

import org.jdom.Element;

import com.sos.joe.jobdoc.editor.forms.ParamsForm;
import com.sos.joe.xml.jobdoc.DocumentationDom;

public class PayloadListener extends JobDocBaseListener<DocumentationDom> {

    private Element _payload;
    private Element _document;
    private ParamsForm _paramsForm;

    public PayloadListener(DocumentationDom dom, Element parent, ParamsForm paramsForm) {
        _dom = dom;
        _parent = parent;
        _paramsForm = paramsForm;
        _payload = _parent.getChild("payload", _dom.getNamespace());
        if (_payload != null) {
            _document = _payload.getChild("document", _dom.getNamespace());
        }
    }

    public void setPayload() {
        if (_payload == null) {
            _payload = new Element("payload", _dom.getNamespace());
            _parent.addContent(_payload);
        }
    }

    public void checkPayload() {
        if (_payload != null) {
            _paramsForm.checkParams();
            checkDocumentation();
            boolean remove = true;
            if (_payload.getChild("note", _dom.getNamespace()) != null) {
                remove = false;
            }
            if (_payload.getChild("params", _dom.getNamespace()) != null) {
                remove = false;
            }
            if (_document != null) {
                remove = false;
            }
            if (remove) {
                _payload.detach();
                _payload = null;
            }
        }
    }

    private void checkDocumentation() {
        if (_document != null && _document.getChildren("note", _dom.getNamespace()).isEmpty()) {
            _document.detach();
            _document = null;
        }
    }

    public Element getPayloadElement() {
        setPayload();
        return _payload;
    }

    private void setDocumentation() {
        if (_document == null) {
            if (_payload == null) {
                setPayload();
            }
            _document = new Element("document", _dom.getNamespace());
            _payload.addContent(_document);
        }
    }

    public Element getDocumentationElement() {
        setDocumentation();
        return _document;
    }

}