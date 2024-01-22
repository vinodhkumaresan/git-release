import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class SendMail {
	
    Properties prop = new Properties();
    private String mailResource = "mail.properties";
    private String smtpHost = "mail.smtp.host";
    private String smtpPort = "mail.smtp.port";
    private String mailTo = "mail.to";
    private String mailCc = "mail.cc";
    private String mailFrom = "mail.from";
    private String mailBcc = "mail.bcc";
    
    public SendMail(String gitHubPjt, String message) {
		
    	Properties mailProp = ReadProperties(mailResource);
    	postMail(mailProp, gitHubPjt, message);
	}
	
	public Properties ReadProperties(String propFilename) {
		
		try (final InputStream input 
				= new FileInputStream("./"+propFilename);) {

			prop.load(input);
            input.close();
            
            return prop;

        } catch (Exception ex) {
        	System.out.println(ex.toString());
            ex.printStackTrace();
        }
		return null;
	}
	
	
	public void postMail(Properties prop, String gitHubProject, String message )
	{
	  	try
	  	{
			Properties props = new Properties();			
			props.put( "mail.smtp.host", prop.getProperty(smtpHost));
			props.put("mail.smtp.port", prop.getProperty(smtpPort));
			Properties test=System.getProperties();
			Session session = Session.getDefaultInstance( props );
            
            MimeMessage msg = new MimeMessage( session );
			InternetAddress addressFrom = new InternetAddress( prop.getProperty(mailFrom) );
			msg.setFrom( addressFrom );
			//Single recipient
			//InternetAddress addressTo = new InternetAddress( prop.getProperty(mailTo) );
			//msg.setRecipient( Message.RecipientType.TO, addressTo );
			
			//Multiple recipient
			String[] recipients = prop.getProperty(mailTo).split(",");
		       
			// Add multiple recipients using an array of InternetAddress objects
            InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                recipientAddresses[i] = new InternetAddress(recipients[i]);
            }
            msg.setRecipients(Message.RecipientType.TO, recipientAddresses);

            
			InternetAddress addressCC = new InternetAddress( prop.getProperty(mailCc) );
			
			msg.setRecipient(Message.RecipientType.CC, addressCC );
			String recipientbcc = prop.getProperty(mailBcc);
			if (recipientbcc.equals("")==false)
				{
					InternetAddress addressBCC = new InternetAddress( recipientbcc );
					msg.setRecipient(Message.RecipientType.BCC, addressBCC );
				}
			msg.setSubject( "GitHub new version Found for : \""+gitHubProject+"\"" );
			msg.setContent( "\""+gitHubProject+"\" : "+message, "text/plain" );
			
			Transport.send( msg );
			System.out.println("Mail Sent");
	  	}
	  	catch(Exception e)
	  	{
			System.out.println(e.toString());
	  	}
	}

}
