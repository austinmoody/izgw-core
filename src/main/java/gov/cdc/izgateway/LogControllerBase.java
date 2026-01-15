package gov.cdc.izgateway;

import ch.qos.logback.classic.spi.ILoggingEvent;
import gov.cdc.izgateway.logging.MemoryAppender;
import gov.cdc.izgateway.logging.RequestContext;
import gov.cdc.izgateway.logging.event.LogEvent;
import gov.cdc.izgateway.security.AccessControlRegistry;
import gov.cdc.izgateway.security.Roles;
import gov.cdc.izgateway.soap.fault.SecurityFault;
import gov.cdc.izgateway.utils.ListConverter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.List;

/**
 * Gives access to in-memory logs on the server. Mostly used for integration
 * testing to make sure log output looks right when sending messages.
 */

// TODO: Blacklisted users can still hit this endpoint because blacklisting only
// applies to the SOAP stack right now. When we extend it to the full HTTP stack,
// we'll need a secure way to clear the blacklist state for test users. Until then,
// this is a bit of a loophole.
public class LogControllerBase implements InitializingBean {

	private MemoryAppender logData = null;

	protected LogControllerBase() {
	}

	@Override
	public void afterPropertiesSet() {
		logData = MemoryAppender.getInstance("memory");
	}

	// TODO: Same blacklist loophole as noted above - see class comment.
	protected List<LogEvent> getLogs(String search) {

		List<ILoggingEvent> events;
		if (logData == null) {
			events = Collections.emptyList();
		} else if (StringUtils.isBlank(search)) {
			events = logData.getLoggedEvents();
		} else {
			events = logData.search(search);
		}
		
		return new ListConverter<>(events, LogEvent::new);
	}

	public void deleteLogs(HttpServletRequest servletReq, String clear) throws SecurityFault {
        if (!RequestContext.getRoles().contains(Roles.ADMIN) && !RequestContext.getRoles().contains(Roles.OPERATIONS)) {
            throw SecurityFault
                    .generalSecurity("Delete Log Attempt By Role",
                            RequestContext.getRoles().toString(), null);
        }

		if (logData != null) {
			logData.reset();
		}
	}
	
}
