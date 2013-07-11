package org.hdiv.web.hateoas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Component;

@Component
public class HdivResourcesProcessor extends AbstractHdivResourceProcessor implements ResourceProcessor<Resources<Object>> {

	private static final Log log = LogFactory.getLog(HdivResourcesProcessor.class);
	
	public Resources<Object> process(Resources<Object> resources) {
		log.debug("Processing resources: " + resources);
		
		doProcessResource(resources);
		
		return resources;
	}

}
