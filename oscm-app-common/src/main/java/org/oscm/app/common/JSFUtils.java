/*******************************************************************************
 *                                                                              
 *  Copyright FUJITSU LIMITED 2017
 *                                                                              
 *  Creation Date: 05.12.2014                                                      
 *                                                                              
 *******************************************************************************/

package org.oscm.app.common;

import java.util.Iterator;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class which provides helper methods around the JSF classes.
 * 
 */
public class JSFUtils {

    /**
     * Verifies that the view locale is equal to the user's locale
     * 
     */
    public static void verifyViewLocale() {
        FacesContext fc = FacesContext.getCurrentInstance();

        HttpServletRequest request = (HttpServletRequest) fc
                .getExternalContext().getRequest();
        HttpSession session = request.getSession();
        String localeString = null;
        if (session != null) {
            localeString = (String) session.getAttribute("loggedInUserLocale");
        }

        // if the view locale differs from the users locale change the view
        // locale
        Locale locale = fc.getViewRoot().getLocale();
        if (localeString != null && !locale.toString().equals(localeString)) {
            Iterator<Locale> it = fc.getApplication().getSupportedLocales();
            while (it.hasNext()) {
                locale = it.next();
                if (locale.toString().equals(localeString)) {
                    fc.getViewRoot().setLocale(locale);
                    return;
                }
            }
            // we use the default locale if the requested locale was not
            // found
            if (!fc.getViewRoot().getLocale()
                    .equals(fc.getApplication().getDefaultLocale())) {
                fc.getViewRoot().setLocale(
                        fc.getApplication().getDefaultLocale());
            }
        }
    }
}
