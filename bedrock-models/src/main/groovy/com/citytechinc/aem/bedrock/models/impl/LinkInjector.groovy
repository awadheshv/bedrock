package com.citytechinc.aem.bedrock.models.impl

import java.lang.reflect.AnnotatedElement

import org.apache.commons.lang3.StringUtils
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.api.link.builders.LinkBuilder
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.core.link.builders.factory.LinkBuilderFactory
import com.citytechinc.aem.bedrock.models.annotations.LinkInject
import com.google.common.base.Optional
import org.osgi.framework.Constants

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
public class LinkInjector extends AbstractTypedComponentNodeInjector<Link> implements Injector,
InjectAnnotationProcessorFactory, AcceptsNullName {

	@Override
	public String getName() {
		return LinkInject.NAME
	}

	@Override
	public Object getValue(ComponentNode componentNode, String name, Class<Link> declaredType,
			AnnotatedElement element, DisposalCallbackRegistry callbackRegistry) {
		LinkInject injectAnnotation = element.getAnnotation(LinkInject.class)
		Optional<String> pathOptional
		String title
		if (injectAnnotation) {
			if (injectAnnotation.inherit()) {
				pathOptional = componentNode.getInherited(name, String.class)
				if (StringUtils.isNotEmpty(injectAnnotation.titleProperty())) {
					title = componentNode.getInherited(injectAnnotation.titleProperty(), String.class).orNull()
				}
			} else {
				pathOptional = componentNode.get(name, String.class)
				if (StringUtils.isNotEmpty(injectAnnotation.titleProperty())) {
					title = componentNode.getInherited(injectAnnotation.titleProperty(), String.class).orNull()
				}
			}
		} else {
			pathOptional = componentNode.get(name, String.class)
		}

		if (pathOptional.isPresent()) {
			LinkBuilder linkBuilder = LinkBuilderFactory.forPath(pathOptional.get()).setTitle(title)

			return linkBuilder.build()
		}

		return null
	}

	@Override
	public InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
		// check if the element has the expected annotation
		LinkInject annotation = element.getAnnotation(LinkInject.class)

		return annotation != null ? new LinkAnnotationProcessor(annotation) : null
	}

	private static class LinkAnnotationProcessor extends AbstractInjectAnnotationProcessor {

		private final LinkInject annotation

		public LinkAnnotationProcessor(LinkInject annotation) {
			this.annotation = annotation
		}

		@Override
		public Boolean isOptional() {
			return annotation.optional()
		}
	}
}
