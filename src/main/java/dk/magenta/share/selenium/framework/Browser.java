package dk.magenta.share.selenium.framework;

import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * Class acting as a danish firefox browser
 * @author Søren Kirkegård
 *
 */
public class Browser {
	
	private final static int NUM_SECS_WAIT_FOR_ALERT = 5;
	private final static int NUM_SECS_WAIT_FOR_ELEM_DISPLAY = 3;
    private static final FirefoxProfile profile = profile();
    private static final FirefoxBinary binary = binary();

    public static WebDriver Driver = null;
    
    /**
     * PhantomJSDriver settings/capabilities
     * @return
     */
    public static DesiredCapabilities dCaps() {
        DesiredCapabilities dCaps = new DesiredCapabilities();
        dCaps.setJavascriptEnabled(true);
        dCaps.setCapability("takesScreenshot", false);
        dCaps.setCapability("phantomjs.page.customHeaders.Accept-Language", "da-DK");
        return dCaps;
    }

    /**
     * This locates the firefox binary and runs it Xvfb. It assumes that
     * Firefox is installed in the default package.
     * CARVEAT! You need to have Xvfb installed:
     * sudo apt-get install xfvb
     * And running
     * Xvfb :1 -screen 0 1024x768x24 &
     * Information about Xfvb see:
     * http://www.x.org/releases/current/doc/man/man1/Xvfb.1.xhtml
     * Read this blog post for more information:
     * http://www.seleniumtests.com/2012/04/headless-tests-with-firefox-webdriver.html
     * @return FirefoxBinary
     */
    private static FirefoxBinary binary() {
        // Setup firefox binary to start in Xvfb
        String Xport = System.getProperty(
                "lmportal.xvfb.id", ":1");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.setEnvironmentProperty("DISPLAY", Xport);


        return firefoxBinary;
    }

    private static FirefoxProfile profile() {

        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("intl.accept_languages", "da");

        return profile;
    }

    /**
     * Static helper method to create a fresh driver for our tests
     * @return
     */
    public static void initialize() {
    	
        boolean headless = false;
        if(headless) {
            Driver =  new FirefoxDriver(binary, profile); System.err.println("headless");
        } else if(!headless) {
            Driver =  new FirefoxDriver(profile);
        } else {
            Driver = new FirefoxDriver(binary, profile); System.err.println("headless");
        }
        
    }

    public static void open(final String url) {
        Driver.get(url);
    }

    public static void close() {
        Driver.close();
    }

    public static String title() {
        return Driver.getTitle();
    }
    
    public static void waitForAlert() {
		waitForAlert(NUM_SECS_WAIT_FOR_ALERT);
	}

	public static void waitForAlert(int numSec) {
		int i = 0;
		while (i++ < numSec) {
			try {
				Alert alert = Driver.switchTo().alert();
				break;
			} catch (NoAlertPresentException e) {
				try {
					Thread.sleep(1000);
				} catch (Exception e1) {
					e1.printStackTrace();
					return;
				}
				continue;
			}
		}
	}

	public static boolean waitForElementDisplayStatus(WebElement element,
			boolean displayStatus, int numSec) {

		int i = 0;
		while (i++ < numSec && element.isDisplayed() != displayStatus) {
			try {
				Thread.sleep(1000);
			} catch (Exception e1) {
				e1.printStackTrace();
				return false;
			}
			continue;
		}

		return element.isDisplayed() == displayStatus;
	}

	public static boolean waitForElementToBeDisplayed(WebElement element) {
		return waitForElementDisplayStatus(element, true,
				NUM_SECS_WAIT_FOR_ELEM_DISPLAY);
	}

	public static boolean waitForElementToDisapear(WebElement element) {
		return waitForElementDisplayStatus(element, false,
				NUM_SECS_WAIT_FOR_ELEM_DISPLAY);
	}

	public static boolean waitForPageToLoad() {
		ExpectedCondition<Boolean> pageLoad = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript(
						"return document.readyState").equals("complete");
			}
		};

		Wait<WebDriver> wait = new WebDriverWait(Driver, 60);
		try {
			wait.until(pageLoad);
			return true;
		} catch (Throwable pageLoadWaitError) {
			return false;
		}

	}
    

}
