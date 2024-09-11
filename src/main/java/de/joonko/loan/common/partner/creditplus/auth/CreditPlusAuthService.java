package de.joonko.loan.common.partner.creditplus.auth;

import de.joonko.loan.config.CreditPlusConfig;
import de.joonko.loan.partner.creditPlus.CreditPlusDefaults;
import de.joonko.loan.partner.creditPlus.stub.EfinComparerServiceStub;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreditPlusAuthService {

    private final CreditPlusConfig creditPlusConfig;

    public OMElement getServiceClient() throws Exception {
        OMFactory omFactory = OMAbstractFactory.getOMFactory();
        OMElement omSecurityElement = omFactory.createOMElement(new QName(CreditPlusDefaults.SECURITY_NAMESPACE, "Security", "wsse"), null);
        OMElement omusertoken = omFactory.createOMElement(new QName("wsse:UsernameToken"), null);
        OMElement omuserName = omFactory.createOMElement(new QName("wsse:Username"), null);
        OMElement omPassword = omFactory.createOMElement(new QName("wsse:Password"), null);
        omPassword.addAttribute("Type", CreditPlusDefaults.USERNAME_TOKEN_NAMESPACE, null);
        OMElement omNonce = omFactory.createOMElement(new QName("wsse:Nonce"), null);
        omNonce.addAttribute("EncodingType", CreditPlusDefaults.ENCODING_NAMESPACE, null);
        OMElement omCreated = omFactory.createOMElement(new QName(CreditPlusDefaults.SECURITY_UTILITY_NAMESPACE, "Created", "wsu"), null);
        String nonce = generateNonce();
        String created = getCreated();
        String password = buildPasswordDigest(nonce, created, creditPlusConfig.getPassword());
        omuserName.setText(creditPlusConfig.getUsername());
        omPassword.setText(password);
        omNonce.setText(nonce);
        omCreated.setText(created);
        omusertoken.addChild(omuserName);
        omusertoken.addChild(omPassword);
        omusertoken.addChild(omNonce);
        omusertoken.addChild(omCreated);
        omSecurityElement.addChild(omusertoken);
        return omSecurityElement;
    }

    private static String buildPasswordDigest(String nonce, String created, String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1;
        String passwordDigest = null;

        try {
            sha1 = MessageDigest.getInstance("SHA-1");
            sha1.update(Base64.decodeBase64(nonce));
            sha1.update(created.getBytes("UTF-8"));
            passwordDigest = new String(Base64.encodeBase64(sha1.digest(password.getBytes("UTF-8"))));
            sha1.reset();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return passwordDigest;
    }

    private static String getCreated() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormatter.format(today);

    }

    private static String generateNonce() {
        String dateTimeString = Long.toString(new Date().getTime());
        byte[] nonceByte = dateTimeString.getBytes();
        return Base64.encodeBase64String(nonceByte);
    }

    private static String getErrorMessages(EfinComparerServiceStub.ErrorItem[] errors) {
        return Arrays.stream(errors).map(
                errorItem -> errorItem.getField().concat("-").concat(errorItem.getMessage())
        ).collect(Collectors.joining("--"));
    }
}
