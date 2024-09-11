/**
 * FaultException.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.santander.stub;

public class FaultException extends java.lang.Exception {
    private static final long serialVersionUID = 1597922267485L;
    private de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.Fault faultMessage;

    public FaultException() {
        super("FaultException");
    }

    public FaultException(java.lang.String s) {
        super(s);
    }

    public FaultException(java.lang.String s, java.lang.Throwable ex) {
        super(s, ex);
    }

    public FaultException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.Fault msg) {
        faultMessage = msg;
    }

    public de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.Fault getFaultMessage() {
        return faultMessage;
    }
}
