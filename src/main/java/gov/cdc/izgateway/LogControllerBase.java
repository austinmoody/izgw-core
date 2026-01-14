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
 * RIGHT, LISTEN UP! This class serves up in-memory logs from the server.
 * It's for integration testing - making sure your log output isn't absolute
 * RUBBISH when you send messages. Simple as that. Beautiful.
 */

// TODO: Oh for crying out loud! Blacklisted users can STILL access this endpoint
// because some DONKEY only applied blacklisting to the SOAP stack! When we extend
// it to the full HTTP stack, we need a PROPER secure way to clear the blacklist
// for test users. This loophole is RAW and it's EMBARRASSING. Sort it out!
public class LogControllerBase implements InitializingBean {

	private MemoryAppender logData = null;

	protected LogControllerBase() {
	}

	@Override
	public void afterPropertiesSet() {
		logData = MemoryAppender.getInstance("memory");
	}

	// TODO: Same bloody loophole as above. READ THE CLASS COMMENT. I'm not repeating myself!
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
