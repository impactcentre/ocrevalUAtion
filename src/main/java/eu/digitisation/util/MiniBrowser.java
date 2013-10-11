/**
 * Copyright (C) 2012 Rafael C. Carrasco (carrasco@ua.es)
 * This code can be distributed or modified
 * under the terms of the GNU General Public License V3.
 */
package eu.digitisation.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import java.net.URL;
import java.util.ArrayList;

/**
 * A simple web browser for checking how results wil be displayed.
 * @version 2012.06.07
 */
public class MiniBrowser extends JFrame
    implements HyperlinkListener {
    static final long serialVersionUID = 1L;
    JButton backButton, forwardButton; // Buttons for page list.
    JTextField locationTextField;      // Page location text field.
    JEditorPane displayEditorPane;     // Editor pane for displaying pages.
    ArrayList<String> pageList =       // List of visited pages 
	new ArrayList<String>();
    
    // Default constructor
    public MiniBrowser() {
        super("browser");   // Application title.
        setSize(800, 600);  // Window size.      
	addWindowListener(new WindowAdapter() {  // Handle closing events.
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });     
        // Set up file menu.
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit",
                KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        
        // Set up button panel.
        JPanel buttonPanel = new JPanel();
        backButton = new JButton("< Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionBack();
            }
        });
        backButton.setEnabled(false);
        buttonPanel.add(backButton);
        forwardButton = new JButton("Forward >");
        forwardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionForward();
            }
        });
        forwardButton.setEnabled(false);
        buttonPanel.add(forwardButton);
        locationTextField = new JTextField(35);
        locationTextField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    actionGo();
                }
            }
        });
        buttonPanel.add(locationTextField);
        JButton goButton = new JButton("GO");
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                actionGo();
            }
        });
        buttonPanel.add(goButton);
        
        // Set up page display.
        displayEditorPane = new JEditorPane();
        displayEditorPane.setContentType("text/html");
        displayEditorPane.setEditable(false);
        displayEditorPane.addHyperlinkListener(this);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(displayEditorPane),
                BorderLayout.CENTER);
    }
    
    // Exit this program.
    private void actionExit() {
        System.exit(0);
    }
    
    // Go back one page.
    private void actionBack() {
        URL currentUrl = displayEditorPane.getPage();
        int pageIndex = pageList.indexOf(currentUrl.toString());
        try {
	    URL backUrl = new URL(pageList.get(pageIndex - 1));
	    showPage(backUrl, false);
        } catch (Exception e) {}
    }
    
    // Go forward one page.
    private void actionForward() {
        URL currentUrl = displayEditorPane.getPage();
        int pageIndex = pageList.indexOf(currentUrl.toString());
        try {
	    URL forwardURL = new URL(pageList.get(pageIndex + 1));
            showPage(forwardURL, false);
        } catch (Exception e) {}
    }
    
    // Load and show the page specified in the location text field.
    private void actionGo() {
        URL verifiedUrl = verifyUrl(locationTextField.getText());
        if (verifiedUrl != null) {
            showPage(verifiedUrl, true);
        } else {
            showError("Invalid URL");
        }
    }
    
    // Show dialog box with error message.
    private void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage,
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Verify URL format.
    private URL verifyUrl(String url) {
	/*        if (!url.toLowerCase().startsWith("http://"))
            return null;
	*/
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }
        
        return verifiedUrl;
    }
    
  /* Show page */
    private void showPage(URL pageUrl, boolean addToList) {
        // Show hour glass cursor while crawling is under way.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            URL currentUrl = displayEditorPane.getPage();
	    displayEditorPane.setPage(pageUrl);
	    URL newUrl = displayEditorPane.getPage();
            
	    if (addToList) {
                int listSize = pageList.size();
                if (listSize > 0) {
                    int pageIndex =
                            pageList.indexOf(currentUrl.toString());
                    if (pageIndex < listSize - 1) {
                        for (int i = listSize - 1; i > pageIndex; i--) {
                            pageList.remove(i);
                        }
                    }
                }
                pageList.add(newUrl.toString());
            }
            
            // Update location text field with URL of current page.
            locationTextField.setText(newUrl.toString());
            
            // Update buttons based on the page being displayed.
            updateButtons();
        } catch (Exception e) {
	    showError("Unable to load page");
        } finally {
            // Return to default cursor.
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
  /* Update back and forward buttons */
    private void updateButtons() {
        if (pageList.size() < 2) {
            backButton.setEnabled(false);
            forwardButton.setEnabled(false);
        } else {
            URL currentUrl = displayEditorPane.getPage();
            int pageIndex = pageList.indexOf(currentUrl.toString());
            backButton.setEnabled(pageIndex > 0);
            forwardButton.setEnabled(
                    pageIndex < (pageList.size() - 1));
        }
    }
    
    // Handle hyperlink's being clicked.
    public void hyperlinkUpdate(HyperlinkEvent event) {
        HyperlinkEvent.EventType eventType = event.getEventType();
        if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (event instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent linkEvent =
                        (HTMLFrameHyperlinkEvent) event;
                HTMLDocument document =
                        (HTMLDocument) displayEditorPane.getDocument();
                document.processHTMLFrameHyperlinkEvent(linkEvent);
            } else {
                showPage(event.getURL(), true);
            }
        }
    }

   

    /**
     * Open a specific page
     */
    public void addPage (String location) {
	try {
	    showPage(new URL(location), true);
	} catch (java.net.MalformedURLException ex) {
	    System.err.println("Invalid URL " + location);
	}
    }

    /**
     * Open a specific page
     */
    public void display (String url) {
	addPage(url);
	setVisible(true);
    }

    // Test the browser.
    public static void main(String[] args) {
	MiniBrowser browser = new MiniBrowser();
	for (String arg: args) {
	    browser.addPage("file:" + arg);
	}
	browser.setVisible(true);
    }
}