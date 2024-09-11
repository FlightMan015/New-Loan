/**
 * ScbCapsDocsWSCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.santander.stub;


/**
 *  ScbCapsDocsWSCallbackHandler Callback class, Users can extend this class and implement
 *  their own receiveResult and receiveError methods.
 */
public abstract class ScbCapsDocsWSCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     * @param clientData Object mechanism by which the user can pass in user data
     * that will be avilable at the time this callback is called.
     */
    public ScbCapsDocsWSCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public ScbCapsDocsWSCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for getContract method
     * override this method for handling normal response from getContract operation
     */
    public void receiveResultgetContract(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.GetContractResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getContract operation
     */
    public void receiveErrorgetContract(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setWebIdLegitimation method
     * override this method for handling normal response from setWebIdLegitimation operation
     */
    public void receiveResultsetWebIdLegitimation(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.SetWebIdLegitimationResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setWebIdLegitimation operation
     */
    public void receiveErrorsetWebIdLegitimation(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getMissingDocuments method
     * override this method for handling normal response from getMissingDocuments operation
     */
    public void receiveResultgetMissingDocuments(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.GetMissingDocumentsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getMissingDocuments operation
     */
    public void receiveErrorgetMissingDocuments(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setDocument method
     * override this method for handling normal response from setDocument operation
     */
    public void receiveResultsetDocument(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.SetDocumentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setDocument operation
     */
    public void receiveErrorsetDocument(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getContractList method
     * override this method for handling normal response from getContractList operation
     */
    public void receiveResultgetContractList(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.GetContractListResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getContractList operation
     */
    public void receiveErrorgetContractList(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getPostident method
     * override this method for handling normal response from getPostident operation
     */
    public void receiveResultgetPostident(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.GetPostidentResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getPostident operation
     */
    public void receiveErrorgetPostident(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setQesNotification method
     * override this method for handling normal response from setQesNotification operation
     */
    public void receiveResultsetQesNotification(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.SetQesNotificationResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setQesNotification operation
     */
    public void receiveErrorsetQesNotification(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getAvailableDocuments method
     * override this method for handling normal response from getAvailableDocuments operation
     */
    public void receiveResultgetAvailableDocuments(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.GetAvailableDocumentsResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getAvailableDocuments operation
     */
    public void receiveErrorgetAvailableDocuments(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setAdvertisingAgreement method
     * override this method for handling normal response from setAdvertisingAgreement operation
     */
    public void receiveResultsetAdvertisingAgreement(
        de.joonko.loan.partner.santander.stub.ScbCapsDocsWSStub.SetAdvertisingAgreementResponse result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setAdvertisingAgreement operation
     */
    public void receiveErrorsetAdvertisingAgreement(java.lang.Exception e) {
    }
}
