/**
 * EfinComparerFaultExceptionException.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.creditPlus.stub;

public class EfinComparerFaultExceptionException extends java.lang.Exception {
    private static final long serialVersionUID = 1594046222048L;
    private de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.EfinComparerFaultExceptionE faultMessage;

    public EfinComparerFaultExceptionException() {
        super("EfinComparerFaultExceptionException");
    }

    public EfinComparerFaultExceptionException(java.lang.String s) {
        super(s);
    }

    public EfinComparerFaultExceptionException(java.lang.String s,
        java.lang.Throwable ex) {
        super(s, ex);
    }

    public EfinComparerFaultExceptionException(java.lang.Throwable cause) {
        super(cause);
    }

    public void setFaultMessage(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.EfinComparerFaultExceptionE msg) {
        faultMessage = msg;
    }

    public de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.EfinComparerFaultExceptionE getFaultMessage() {
        return faultMessage;
    }
}
