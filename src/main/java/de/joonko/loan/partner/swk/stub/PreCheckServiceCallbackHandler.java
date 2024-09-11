/**
 * PreCheckServiceCallbackHandler.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.swk.stub;


/**
 *  PreCheckServiceCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class PreCheckServiceCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public PreCheckServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public PreCheckServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for checkForCredit method
     * override this method for handling normal response from checkForCredit operation
     */
    public void receiveResultcheckForCredit(
        de.joonko.loan.partner.swk.stub.PreCheckServiceStub.CheckForCreditResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from checkForCredit operation
     */
    public void receiveErrorcheckForCredit(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceVersion method
     * override this method for handling normal response from getServiceVersion operation
     */
    public void receiveResultgetServiceVersion(
        de.joonko.loan.partner.swk.stub.PreCheckServiceStub.GetServiceVersionResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceVersion operation
     */
    public void receiveErrorgetServiceVersion(java.lang.Exception e) {
    }
}
