package utils.servlet;

import utils.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


public class SessionUtils {
        public static String getUsername (HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            Object sessionAttribute = session != null ? session.getAttribute(Constants.USERNAME) : null;
            return sessionAttribute != null ? sessionAttribute.toString() : null;
        }


        public static String getContestUBoat(HttpServletRequest request){
            HttpSession session = request.getSession(false);
            Object sessionAttribute = session != null ? session.getAttribute(Constants.UBOAT) : null;
            return  sessionAttribute != null ? sessionAttribute.toString() : null;
        }

        public static String getAllyName(HttpServletRequest request){
            HttpSession session = request.getSession(false);
            Object sessionAttribute = session != null ? session.getAttribute(Constants.ALLY) : null;
            return  sessionAttribute != null ? sessionAttribute.toString() : null;
        }


        public static void clearSession (HttpServletRequest request) {
            request.getSession().invalidate();
        }




}

