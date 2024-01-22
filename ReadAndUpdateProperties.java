import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class ReadAndUpdateProperties {
	
	Properties prop = new Properties();
	
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
	
	public void setProperty(String key, String value){
		  prop.setProperty(key, value);
		}

	public void flush(String propFilename) {
		try (final FileOutputStream outputstream 
					= new FileOutputStream(new File("./"+propFilename));) {
			prop.store(outputstream,"File Updated");
			outputstream.close();
		}
		catch (Exception ex) {
        	System.out.println(ex.toString());
            ex.printStackTrace();
        }
	}

}
