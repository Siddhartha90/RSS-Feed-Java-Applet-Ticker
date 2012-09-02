import java.awt.Desktop;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JApplet;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;


class changeDisplay {
	private int len;
    private Timer timer;
    private String feedMsgs[];
    private static int counter=0;
    private JEditorPane editorPane;
    
    public changeDisplay (int seconds, String feedMsgs[], JEditorPane editorPane, int len) {
    	timer = new Timer();
        timer.schedule(new RemindTask(),
                       0,        //initial delay
                       seconds*1000);  //subsequent rate
        
        this.len = len;
        this.feedMsgs = feedMsgs;
        this.editorPane = editorPane;
	}

    String formatContents(String input) {
    	
    	String title="", description="", link="", author="";
    	String rawString = input.substring(13,input.length()-1);
    	StringTokenizer st = new StringTokenizer(rawString, "#;");
    	
    	while(st.hasMoreTokens()) {
    		String key = st.nextToken().trim();
    		String val = st.nextToken().trim();
    		
    		if ( key.equals("title"))
    			title = val;
    		else if ( key.equals("description"))
    			description = val;
    		else if ( key.equals("link"))
    			link = val;
    		else if ( key.equals("author"))
    			author = val;   		
    	}

    	StringWriter sout = new StringWriter();
        PrintWriter out = new PrintWriter(sout);	
        
        ReadTest.class.getClassLoader();
		String imgsrc = ClassLoader.getSystemResource("rss.png").toString();

        out.println("<br />&nbsp;&nbsp;<img src=\'"+imgsrc+"\' width=12 height=12 align=\"left\" />&nbsp;&nbsp;&nbsp;<strong><a href=\""+link+"\" target=\"_blank\">"+title+"</a></strong>&nbsp;&nbsp;&nbsp;");
        out.println("<font size=\"2.25\" color=\"gray\">by "+author+"<br /></font>");
        out.println("&nbsp;&nbsp;<font size=\"3\" color=\"black\">"+description+"</font>");
        out.close();
        
        return sout.toString();
    }
    
    class RemindTask extends TimerTask {
        public void run() {       	
            String output = formatContents(feedMsgs[counter++]);
            editorPane.setText(output);
            counter = counter % len;
        }
    }
}

public class ReadTest extends JApplet 
{
	private Thread runner;
	private String tickerline, displaystring; 
	private JEditorPane editorPane; 
	private RSSFeedParser parser = new RSSFeedParser("http://www.ece.illinois.edu/mediacenter/news_rss.asp");
	private Feed feed = parser.readFeed();
    private int cnt, i=0, delay = 200;
    private boolean isRunning = false;
    private FontMetrics fm;
    private String feedMsgsArray[] = new String[100];;
   
    
    public void init() 
    {   
    	try
		{
    		for (FeedMessage message : feed.getMessages()) {
        		//System.out.println("Received message : "+message.toString());
        		feedMsgsArray[i++] = message.toString();
        	}    		
    			
    		createGUI();
    		new changeDisplay(5, feedMsgsArray, editorPane,i);

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	    
     
   //creates bkground creates screen(editor pane) to put stuff on.
    private void createGUI() throws IOException {
    	        
       editorPane = new JEditorPane();
	   editorPane.setEditable(false);		//setting stuff on editor pane
       editorPane.setContentType("text/html");
	   //editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
	    
        //editorPane.addHyperlinkListener(new ActivatedHyperlinkListener(editorPane));
        // Set up the JEditorPane to handle clicks on hyperlinks
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                   // Do something with e.getURL() here
                	if(Desktop.isDesktopSupported()) {
                	    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                	}
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(editorPane);
    	getContentPane().add(editorPane);
   }    
   
}