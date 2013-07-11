package org.hdiv.web.hateoas;

import java.util.ArrayList;
import java.util.List;

import org.hdiv.AbstractHDIVTestCase;
import org.hdiv.util.HDIVUtil;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HdivResourceSupportProcessorTest extends AbstractHDIVTestCase {
	
	ResourceRequestDataValueProcessor hdivResourceSupportProcessor;
	
	@Override
	protected void onSetUp() throws Exception {
		hdivResourceSupportProcessor = getApplicationContext().getBean(ResourceRequestDataValueProcessor.class);
		ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(HDIVUtil.getHttpServletRequest()); 
		RequestContextHolder.setRequestAttributes(servletRequestAttributes);
	}
	
	@SuppressWarnings("unchecked")
	public void testProcessResource() {
		Resource<Item> resource = new Resource<Item>(new Item(2, "item-b"));
		resource.add(new Link("http://localhost", Link.REL_SELF));
		resource.add(new Link("http://localhost/other", "other-rel"));
		
		Resource<Item> processedResource = (Resource<Item>) hdivResourceSupportProcessor.process(resource);
		assertEquals(resource.getContent(), processedResource.getContent());
		assertEquals(2, processedResource.getLinks().size());
		
		for (Link link : processedResource.getLinks()) {
			assertTrue(link.getHref().contains("_HDIV_STATE_"));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void testProcessResources() {
		List<Item> items = new ArrayList<Item>();
		items.add(new Item(1, "item-a"));
		items.add(new Item(2, "item-b"));
		items.add(new Item(3, "item-c"));
		
		Resources<Item> resources = new Resources<Item>(items);
		
		resources.add(new Link("http://localhost", Link.REL_SELF));
		resources.add(new Link("http://localhost/other", "other-rel"));
		
		Resources<Item> result = (Resources<Item>) hdivResourceSupportProcessor.process(resources);
		assertEquals(3, result.getContent().size());
		assertEquals(2, result.getLinks().size());
		
		for (Link link : result.getLinks()) {
			assertTrue(link.getHref().contains("_HDIV_STATE_"));
		}
	}
	
	public class Item {
		private long id;
		private String name;
		
		public Item(long id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public long getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (int) (id ^ (id >>> 32));
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id != other.id)
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		private HdivResourceSupportProcessorTest getOuterType() {
			return HdivResourceSupportProcessorTest.this;
		}
		
	}

}
