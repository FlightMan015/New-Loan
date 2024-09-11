/**
 * ScbCapsBcoWSCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.santander.stub;


/**
 *  ScbCapsBcoWSCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class ScbCapsBcoWSCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public ScbCapsBcoWSCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public ScbCapsBcoWSCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for getKreditvergleichsangebot method
     * override this method for handling normal response from getKreditvergleichsangebot operation
     */
    public void receiveResultgetKreditvergleichsangebot(
        de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.GetKreditvergleichsangebotResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getKreditvergleichsangebot operation
     */
    public void receiveErrorgetKreditvergleichsangebot(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getKreditvertragsangebot method
     * override this method for handling normal response from getKreditvertragsangebot operation
     */
    public void receiveResultgetKreditvertragsangebot(
        de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.GetKreditvertragsangebotResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getKreditvertragsangebot operation
     */
    public void receiveErrorgetKreditvertragsangebot(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getKreditantragsstatus method
     * override this method for handling normal response from getKreditantragsstatus operation
     */
    public void receiveResultgetKreditantragsstatus(
        de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.GetKreditantragsstatusResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getKreditantragsstatus operation
     */
    public void receiveErrorgetKreditantragsstatus(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getKreditkonditionen method
     * override this method for handling normal response from getKreditkonditionen operation
     */
    public void receiveResultgetKreditkonditionen(
        de.joonko.loan.partner.santander.stub.ScbCapsBcoWSStub.GetKreditkonditionenResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getKreditkonditionen operation
     */
    public void receiveErrorgetKreditkonditionen(java.lang.Exception e) {
    }
}
