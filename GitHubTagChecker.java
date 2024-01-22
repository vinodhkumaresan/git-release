import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Enumeration;
import java.util.Properties;

// java -cp "activation.jar:javax.mail.jar:." GitHubTagChecker
public class GitHubTagChecker {
	
	private String githubResource = "github.properties";
	
	
	public GitHubTagChecker() {
		ReadAndUpdateProperties readAndUpdateProp = new ReadAndUpdateProperties();
		Properties readProp = readAndUpdateProp.ReadProperties(githubResource);
		
		if (readProp != null) {
			Enumeration propEnum = readProp.propertyNames();            
			while (propEnum.hasMoreElements()) {
				//github project name
	            String gitHubkey = (String) propEnum.nextElement();
	            //Current version of project stored in properties file
	            String tagValue = readProp.getProperty(gitHubkey);
	            String newTag = getLatestTag(gitHubkey, tagValue);
	            if (newTag != null) {
	            	new SendMail(gitHubkey, newTag);
		            System.out.println(newTag);
		            readAndUpdateProp.setProperty(gitHubkey, newTag);
		            readAndUpdateProp.flush(githubResource);
	            }
	        }
		}
		else {
			errorMessage("Error in reading the properties file : "+githubResource);
		}
	}
	
	public void errorMessage(String msg) {
		System.out.println(msg);
	}

    public static void main(String[] args) {
    	new GitHubTagChecker();
    }

    private String getLatestTag(String gitHubRepo, String tagVersion) {
        String apiUrl = "https://api.github.com/repos/" + gitHubRepo + "/releases/latest";
        String latestTag = null;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
			//GitHub Authorization. Dont add it while uploading to github
            connection.setRequestProperty("Authorization", "");
            
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response to get the latest tag name
                String gitHubTagVersion = response.toString().split("\"tag_name\":\"")[1].split("\"")[0];               
                
                
                if (gitHubTagVersion != null) {
                    if (!tagVersion.equalsIgnoreCase(gitHubTagVersion)) {
                    	//SendMail
                    	System.out.println("Found a latest tag for \""+gitHubRepo+ "\" : "+gitHubTagVersion);
                    	latestTag = gitHubTagVersion;
                        return latestTag;     
                    }
                    
                } else {
                	errorMessage("Failed to retrieve latest tag.");
                }
            } else {
            	errorMessage("HTTP request failed with response code: " + responseCode);
            }       
            
        } catch (IOException e) {
            e.printStackTrace();
            //return null;
        }
        return null;
    }
}
