package org.hdiv.web.hateoas;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * Implementation of {@link ResourceProcessor} to apply {@link RequestDataValueProcessor} to hateoas resources.
 * 
 * @author inigo
 */
public class ResourceRequestDataValueProcessor implements ResourceProcessor<ResourceSupport> {

	/** Implementation of {@link RequestDataValueProcessor} which processes the resources' links */
	@Autowired RequestDataValueProcessor requestDataValueProcessor;

	public void setRequestDataValueProcessor(RequestDataValueProcessor requestDataValueProcessor) {
		this.requestDataValueProcessor = requestDataValueProcessor;
	}

	/**
	 * Processes {@link Resource} and {@link Resources} types. Any different resource is ignored by this processor.
	 */
	public ResourceSupport process(ResourceSupport resource) {
		if (resource instanceof Resources) {
			processResources((Resources<?>) resource);
		}

		if (resource instanceof Resource) {
			processResource((Resource<?>) resource);
		}

		return resource;
	}

	/**
	 * Processes resources of type {@link Resources}
	 * 
	 * @param resources
	 */
	protected void processResources(Resources<?> resources) {
		processLinks(resources);

		for (Object resourceCandidate : resources.getContent()) {
			if (resourceCandidate instanceof ResourceSupport) {
				process((ResourceSupport) resourceCandidate);
			}
		}
	}

	/**
	 * Processes resources of type {@link Resource}
	 * 
	 * @param resource
	 */
	protected void processResource(Resource<?> resource) {
		processLinks(resource);

		if (resource.getContent() instanceof ResourceSupport) {
			process((ResourceSupport) resource.getContent());
		}
	}

	/**
	 * Every link of the list is processed by the {@link RequestDataValueProcessor}
	 * 
	 * @param links
	 */
	protected void processLinks(ResourceSupport resourceSupport) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		
		List<Link> links = resourceSupport.getLinks();
		List<Link> processedLinks = new ArrayList<Link>();
		
		
		for (Link link : links) {
			String processedUrl = requestDataValueProcessor.processUrl(request, link.getHref());
			processedLinks.add(new Link(processedUrl, link.getRel()));
		}
		
		resourceSupport.removeLinks();
		resourceSupport.add(processedLinks);
	}

}
