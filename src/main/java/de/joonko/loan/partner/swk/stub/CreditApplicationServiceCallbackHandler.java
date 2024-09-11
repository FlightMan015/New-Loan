/**
 * CreditApplicationServiceCallbackHandler.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.swk.stub;


/**
 * CreditApplicationServiceCallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class CreditApplicationServiceCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public CreditApplicationServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public CreditApplicationServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for getApplicationVersion method
     * override this method for handling normal response from getApplicationVersion operation
     */
    public void receiveResultgetApplicationVersion(
        de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.GetApplicationVersionResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getApplicationVersion operation
     */
    public void receiveErrorgetApplicationVersion(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceName method
     * override this method for handling normal response from getServiceName operation
     */
    public void receiveResultgetServiceName(
        de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.GetServiceNameResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceName operation
     */
    public void receiveErrorgetServiceName(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceVersion method
     * override this method for handling normal response from getServiceVersion operation
     */
    public void receiveResultgetServiceVersion(
        de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.GetServiceVersionResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceVersion operation
     */
    public void receiveErrorgetServiceVersion(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getServiceId method
     * override this method for handling normal response from getServiceId operation
     */
    public void receiveResultgetServiceId(
        de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.GetServiceIdResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getServiceId operation
     */
    public void receiveErrorgetServiceId(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for applyForCredit method
     * override this method for handling normal response from applyForCredit operation
     */
    public void receiveResultapplyForCredit(
        de.joonko.loan.partner.swk.stub.CreditApplicationServiceStub.ApplyForCreditResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from applyForCredit operation
     */
    public void receiveErrorapplyForCredit(java.lang.Exception e) {
    }
}
