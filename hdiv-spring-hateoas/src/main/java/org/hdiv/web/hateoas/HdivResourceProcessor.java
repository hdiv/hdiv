package org.hdiv.web.hateoas;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

@Component
public class HdivResourceProcessor extends AbstractHdivResourceProcessor implements ResourceProcessor<Resource<Object>> {

	private static final Log log = LogFactory.getLog(HdivResourceProcessor.class);
	
	public Resource<Object> process(Resource<Object> resource) {
		log.debug("Processing resources: " + resource);
		
		doProcessResource(resource);
		
		return resource;
	}

}
