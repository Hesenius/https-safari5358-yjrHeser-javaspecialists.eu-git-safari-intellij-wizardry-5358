package com.someone.xml;

import com.someone.io.*;
import org.jdom.*;
import org.jdom.input.*;

import java.util.*;


public class SchemaParser {
    private static SchemaParser _instance;
    private Namespace namespace;
    private SchemaElement rootSchemaElement; // used to find 'ref' elements
    private SchemaElement topElement;
    private SchemaElement firstLevel;

    private SchemaParser(final String schemaFile, final String rootName, final String nameSpace,
                         final String nsURL) {
        try {

            final SAXBuilder builder = new SAXBuilder();

            final Document schema = builder.build(FileProxy.getResourceReader(
                schemaFile));
            namespace = Namespace.getNamespace(nameSpace, nsURL);
            final Element documentElement = schema.getRootElement();

            rootSchemaElement = new SchemaElement(documentElement,
                SchemaElement.SIMPLE);
            firstLevel = getElement(rootSchemaElement, rootName);

            final SchemaElement[] choices = getChoices(firstLevel);
            topElement = choices[0];
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    // was getRuleSet()
    public SchemaElement getRootElement() {
        return firstLevel;
    }

    public SchemaElement getFirstLevel() {
        return topElement;
    }

    public static SchemaParser getInstance() {
        return _instance;
    }

    public static SchemaParser getInstance(final String schemaFile, final String rootName,
                                           final String nameSpace, final String nsURL) {
        if (_instance == null) {
            _instance = new SchemaParser(schemaFile, rootName, nameSpace, nsURL);
        }

        return _instance;
    }

    private SchemaElement buildElement(Element ref) {
        boolean compulsory = true;
        SchemaElement result = null;
        String minOccurs = null;

        if (ref.getAttributeValue("ref") != null) {
            minOccurs = ref.getAttributeValue("minOccurs");

            if ((minOccurs != null) && minOccurs.equals("0")) {
                compulsory = false;
            }

            ref = getElement(rootSchemaElement, ref.getAttributeValue("ref"))
                .getElement();
        } else {
            minOccurs = ref.getAttributeValue("minOccurs");

            if ((minOccurs != null) && minOccurs.equals("0")) {
                compulsory = false;
            }
        }

        if (ref.getChild("complexType", namespace) != null) {
            if (ref.getChild("complexType", namespace)
                .getChild("sequence", namespace) != null) {
                result = new SchemaElement(ref, SchemaElement.SEQUENCE);
            } else if (ref.getChild("complexType", namespace)
                .getChild("choice", namespace) != null) {
                result = new SchemaElement(ref, SchemaElement.CHOICE);
            }
        } else {
            result = new SchemaElement(ref, SchemaElement.SIMPLE);
        }

        result.setCompulsory(compulsory);

        return result;
    }

    // <xs:complexType>
    // <xs:sequence>
    // <xs:element name = "RuleSet">
    public SchemaElement[] getChoices(final SchemaElement parent) {
        final ArrayList allNodes = new ArrayList();
        List elements;
        final List complexElements = parent.getElement()
            .getChildren("complexType", namespace);

        for (int i = 0; i < complexElements.size(); i++) {
            final Element el = (Element) complexElements.get(i);
            final List sequences = el.getChildren("sequence", namespace);

            for (int s = 0; s < sequences.size(); s++) {
                final Element seq = (Element) sequences.get(s);
                elements = seq.getChildren("element", namespace);

                for (int k = 0; k < elements.size(); k++) {
                    final Element ref = (Element) elements.get(k);
                    final SchemaElement newElement = buildElement(ref);
                    allNodes.add(newElement);
                }
            }

            final List choices = el.getChildren("choice", namespace);

            for (int c = 0; c < choices.size(); c++) {
                final Element choice = (Element) choices.get(c);
                elements = choice.getChildren("element", namespace);

                for (int k = 0; k < elements.size(); k++) {
                    final Element ref = (Element) elements.get(k);
                    final SchemaElement newElement = buildElement(ref);
                    allNodes.add(newElement);
                }
            }
        }

        elements = parent.getElement().getChildren("element", namespace);

        for (int i = 0; i < elements.size(); i++) {
            final Element ref = (Element) elements.get(i);
            final SchemaElement newElement = buildElement(ref);
            allNodes.add(newElement);
        }

        final SchemaElement[] result = new SchemaElement[allNodes.size()];

        for (int j = 0; j < allNodes.size(); j++) {
            final SchemaElement ell = (SchemaElement) allNodes.get(j);
            result[j] = ell;
        }

        return result;
    }

    private SchemaElement getElement(final SchemaElement parent, final String name) {
        final List l = parent.getElement().getChildren("element", namespace);

        for (int i = 0; i < l.size(); i++) {
            final Element el = (Element) l.get(i);
            final String ename = el.getAttributeValue("name");

            if (name.equals(ename)) {
                return new SchemaElement(el, SchemaElement.SIMPLE);
            }
        }

        return null;
    }
}
