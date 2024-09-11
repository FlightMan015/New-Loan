/**
 * EfinComparerServiceCallbackHandler.java
 * <p>
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
package de.joonko.loan.partner.creditPlus.stub;


/**
 * EfinComparerServiceCallbackHandler Callback class, Users can extend this class and implement
 * their own receiveResult and receiveError methods.
 */
public abstract class EfinComparerServiceCallbackHandler {
    protected Object clientData;

    /**
     * User can pass in any object that needs to be accessed once the NonBlocking
     * Web service call is finished and appropriate method of this CallBack is called.
     *
     * @param clientData Object mechanism by which the user can pass in user data
     *                   that will be avilable at the time this callback is called.
     */
    public EfinComparerServiceCallbackHandler(Object clientData) {
        this.clientData = clientData;
    }

    /**
     * Please use this constructor if you don't want to set any clientData
     */
    public EfinComparerServiceCallbackHandler() {
        this.clientData = null;
    }

    /**
     * Get the client data
     */
    public Object getClientData() {
        return clientData;
    }

    /**
     * auto generated Axis2 call back method for getContracts method
     * override this method for handling normal response from getContracts operation
     */
    public void receiveResultgetContracts(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.GetContractsResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getContracts operation
     */
    public void receiveErrorgetContracts(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for startDispatch method
     * override this method for handling normal response from startDispatch operation
     */
    public void receiveResultstartDispatch(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.StartDispatchResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from startDispatch operation
     */
    public void receiveErrorstartDispatch(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getDocument method
     * override this method for handling normal response from getDocument operation
     */
    public void receiveResultgetDocument(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.GetDocumentResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getDocument operation
     */
    public void receiveErrorgetDocument(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createCreditOffer method
     * override this method for handling normal response from createCreditOffer operation
     */
    public void receiveResultcreateCreditOffer(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.CreateCreditOfferResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createCreditOffer operation
     */
    public void receiveErrorcreateCreditOffer(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for cancelCreditOffer method
     * override this method for handling normal response from cancelCreditOffer operation
     */
    public void receiveResultcancelCreditOffer(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.CancelCreditOfferResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from cancelCreditOffer operation
     */
    public void receiveErrorcancelCreditOffer(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for getDocumentVideoIdent method
     * override this method for handling normal response from getDocumentVideoIdent operation
     */
    public void receiveResultgetDocumentVideoIdent(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.GetDocumentVideoIdentResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from getDocumentVideoIdent operation
     */
    public void receiveErrorgetDocumentVideoIdent(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for createCreditOfferDac method
     * override this method for handling normal response from createCreditOfferDac operation
     */
    public void receiveResultcreateCreditOfferDac(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.CreateCreditOfferDacResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from createCreditOfferDac operation
     */
    public void receiveErrorcreateCreditOfferDac(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for setVideoIdentExternData method
     * override this method for handling normal response from setVideoIdentExternData operation
     */
    public void receiveResultsetVideoIdentExternData(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.SetVideoIdentExternDataResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from setVideoIdentExternData operation
     */
    public void receiveErrorsetVideoIdentExternData(java.lang.Exception e) {
    }

    /**
     * auto generated Axis2 call back method for activateCommunication method
     * override this method for handling normal response from activateCommunication operation
     */
    public void receiveResultactivateCommunication(
        de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub.ActivateCommunicationResponseE result) {
    }

    /**
     * auto generated Axis2 Error handler
     * override this method for handling error response from activateCommunication operation
     */
    public void receiveErroractivateCommunication(java.lang.Exception e) {
    }
}
