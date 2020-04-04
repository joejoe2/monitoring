/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handlingserver;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
/**
 *
 * @author 70136
 */
public class MyX509TrustManager implements X509TrustManager {
@Override
public void checkClientTrusted(X509Certificate certificates[],String authType) throws CertificateException {
}
@Override
public void checkServerTrusted(X509Certificate[] ax509certificate,String s) throws CertificateException {
}
@Override
public X509Certificate[] getAcceptedIssuers() {
// TODO Auto-generated method stub
return null;
}
}
