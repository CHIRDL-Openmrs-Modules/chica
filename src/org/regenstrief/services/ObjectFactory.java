
package org.regenstrief.services;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.regenstrief.services package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetDumpResponse_QNAME = new QName("http://www.regenstrief.org/services", "getDumpResponse");
    private final static QName _GetDump_QNAME = new QName("http://www.regenstrief.org/services", "getDump");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.regenstrief.services
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetDump }
     * 
     */
    public GetDump createGetDump() {
        return new GetDump();
    }

    /**
     * Create an instance of {@link GetDumpResponse }
     * 
     */
    public GetDumpResponse createGetDumpResponse() {
        return new GetDumpResponse();
    }

    /**
     * Create an instance of {@link EntityIdentifier }
     * 
     */
    public EntityIdentifier createEntityIdentifier() {
        return new EntityIdentifier();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDumpResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.regenstrief.org/services", name = "getDumpResponse")
    public JAXBElement<GetDumpResponse> createGetDumpResponse(GetDumpResponse value) {
        return new JAXBElement<GetDumpResponse>(_GetDumpResponse_QNAME, GetDumpResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDump }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.regenstrief.org/services", name = "getDump")
    public JAXBElement<GetDump> createGetDump(GetDump value) {
        return new JAXBElement<GetDump>(_GetDump_QNAME, GetDump.class, null, value);
    }

}
