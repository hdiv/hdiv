package org.hdiv.web.hateoas;

import java.lang.reflect.Field;
import java.util.List;

import org.hdiv.util.HDIVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

@SuppressWarnings("unchecked")
public abstract class AbstractHdivResourceProcessor {
	
	private static final Field HREF_FIELD = ReflectionUtils.findField(Link.class, "href");
	
	@Autowired RequestDataValueProcessor requestDataValueProcessor;

	protected void doProcessResource(Object resource) {
		if (!(resource instanceof ResourceSupport)) {
			return;
		}
		
		if (resource instanceof Resources) {
			processResources((Resources<Object>)resource);
		}
		
		if (resource instanceof Resource) {
			processResource((Resource<Object>)resource);
		}
	}
	
	protected void processResources(Resources<Object> resources) {
		processLink(resources.getLinks());
		
		for (Object resourceCandidate : resources.getContent()) {
			doProcessResource(resourceCandidate);
		}
	}
	
	protected void processResource(Resource<Object> resource) {	
		processLink(resource.getLinks());
		doProcessResource(resource.getContent());
	}
	
	private void processLink(List<Link> links) {
		for (Link link : links) {
			String processedUrl = requestDataValueProcessor.processUrl(HDIVUtil.getHttpServletRequest(), link.getHref());
			ReflectionUtils.makeAccessible(HREF_FIELD);
			ReflectionUtils.setField(HREF_FIELD, link, processedUrl);
		}
	}
	
}
