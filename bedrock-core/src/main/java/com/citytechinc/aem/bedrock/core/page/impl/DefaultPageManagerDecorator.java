package com.citytechinc.aem.bedrock.core.page.impl;

import com.citytechinc.aem.bedrock.api.page.PageDecorator;
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator;
import com.citytechinc.aem.bedrock.core.page.predicates.TemplatePredicate;
import com.citytechinc.aem.bedrock.core.utils.PathUtils;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Revision;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.msm.Blueprint;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@SuppressWarnings("deprecation")
public final class DefaultPageManagerDecorator implements PageManagerDecorator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPageManagerDecorator.class);

    private final ResourceResolver resourceResolver;

    private final PageManager pageManager;

    public DefaultPageManagerDecorator(final ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;

        pageManager = resourceResolver.adaptTo(PageManager.class);
    }

    @Override
    public List<PageDecorator> findPages(final String path, final Collection<String> tagIds, final boolean matchOne) {
        checkNotNull(path);
        checkNotNull(tagIds);

        LOG.debug("path = {}, tag IDs = {}", path, tagIds);

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final RangeIterator<Resource> iterator = resourceResolver.adaptTo(TagManager.class).find(path, tagIds.toArray(
            new String[tagIds.size()]), matchOne);

        final List<PageDecorator> pages = Lists.newArrayList();

        if (iterator != null) {
            while (iterator.hasNext()) {
                final Resource resource = iterator.next();

                if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                    final PageDecorator page = getPage(resource.getParent().getPath());

                    if (page != null) {
                        pages.add(page);
                    }
                }
            }
        }

        stopwatch.stop();

        LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));

        return pages;
    }

    @Override
    public List<PageDecorator> search(final Query query) {
        return search(query, -1);
    }

    @Override
    public List<PageDecorator> search(final Query query, final int limit) {
        checkNotNull(query);

        LOG.debug("query statement = {}", query.getStatement());

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final List<PageDecorator> pages = Lists.newArrayList();

        int count = 0;

        try {
            final QueryResult result = query.execute();
            final RowIterator rows = result.getRows();

            final Set<String> paths = new HashSet<String>();

            while (rows.hasNext()) {
                if (count > -1 && count == limit) {
                    break;
                }

                final Row row = rows.nextRow();
                final String path = row.getPath();

                LOG.debug("result path = {}", path);

                final String pagePath = PathUtils.getPagePath(path);

                // ensure no duplicate pages are added
                if (!paths.contains(pagePath)) {
                    paths.add(pagePath);

                    final PageDecorator page = decoratePage(path);

                    if (page == null) {
                        LOG.error("result is null for path = {}", path);
                    } else {
                        pages.add(page);

                        count++;
                    }
                }
            }

            stopwatch.stop();

            LOG.debug("found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));
        } catch (RepositoryException re) {
            LOG.error("error finding pages for query = " + query.getStatement(), re);
        }

        return pages;
    }

    @Override
    public List<PageDecorator> findPages(final String path, final String template) {
        return findPages(path, new TemplatePredicate(template));
    }

    @Override
    public List<PageDecorator> findPages(final String path, final Predicate<PageDecorator> predicate) {
        checkNotNull(path);

        final PageDecorator page = getPage(path);

        final Stopwatch stopwatch = Stopwatch.createStarted();

        final List<PageDecorator> result;

        if (page == null) {
            result = Collections.emptyList();
        } else {
            result = page.findDescendants(predicate);
        }

        stopwatch.stop();

        LOG.debug("found {} result(s) in {}ms", result.size(), stopwatch.elapsed(MILLISECONDS));

        return result;
    }

    @Override
    public PageDecorator copy(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict) throws WCMException {
        return decoratePage(pageManager.copy(page, destination, beforeName, shallow, resolveConflict));
    }

    @Override
    public PageDecorator copy(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict, final boolean autoSave) throws WCMException {
        return decoratePage(pageManager.copy(page, destination, beforeName, shallow, resolveConflict, autoSave));
    }

    @Override
    public PageDecorator create(final String parentPath, final String pageName, final String template,
        final String title) throws WCMException {
        return decoratePage(pageManager.create(parentPath, pageName, template, title));
    }

    @Override
    public PageDecorator create(final String parentPath, final String pageName, final String template,
        final String title, final boolean autoSave) throws WCMException {
        return decoratePage(pageManager.create(parentPath, pageName, template, title, autoSave));
    }

    @Override
    public PageDecorator getContainingPage(final Resource resource) {
        return decoratePage(pageManager.getContainingPage(resource));
    }

    @Override
    public PageDecorator getContainingPage(final String path) {
        return decoratePage(pageManager.getContainingPage(path));
    }

    @Override
    public PageDecorator getPage(final Page page) {
        return decoratePage(page);
    }

    @Override
    public PageDecorator getPage(final String path) {
        return decoratePage(checkNotNull(path));
    }

    @Override
    public PageDecorator move(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict, final String[] adjustRefs) throws WCMException {
        return decoratePage(pageManager.move(page, destination, beforeName, shallow, resolveConflict, adjustRefs));
    }

    @Override
    public PageDecorator restore(final String path, final String revisionId) throws WCMException {
        return decoratePage(pageManager.restore(path, revisionId));
    }

    @Override
    public PageDecorator restoreTree(final String path, final Calendar date) throws WCMException {
        return decoratePage(pageManager.restoreTree(path, date));
    }

    @Override
    public Resource copy(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict) throws WCMException {
        return pageManager.copy(resource, destination, beforeName, shallow, resolveConflict);
    }

    @Override
    public Resource copy(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict, final boolean autoSave) throws WCMException {
        return pageManager.copy(resource, destination, beforeName, shallow, resolveConflict, autoSave);
    }

    @Override
    public Revision createRevision(final Page page) throws WCMException {
        return pageManager.createRevision(page);
    }

    @Override
    public Revision createRevision(final Page page, final String label, final String comment) throws WCMException {
        return pageManager.createRevision(page, label, comment);
    }

    @Override
    public void delete(final Page page, final boolean shallow) throws WCMException {
        pageManager.delete(page, shallow);
    }

    @Override
    public void delete(final Page page, final boolean shallow, final boolean autoSave) throws WCMException {
        pageManager.delete(page, shallow, autoSave);
    }

    @Override
    public void delete(final Resource resource, final boolean shallow) throws WCMException {
        pageManager.delete(resource, shallow);
    }

    @Override
    public void delete(final Resource resource, final boolean shallow, final boolean autoSave) throws WCMException {
        pageManager.delete(resource, shallow, autoSave);
    }

    @Override
    public Collection<Blueprint> getBlueprints(final String parentPath) {
        return pageManager.getBlueprints(parentPath);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final Calendar cal) throws WCMException {
        return pageManager.getChildRevisions(parentPath, cal);
    }

    @Override
    public Collection<Revision> getRevisions(final String parentPath, final Calendar cal) throws WCMException {
        return pageManager.getRevisions(parentPath, cal);
    }

    @Override
    public Template getTemplate(final String templatePath) {
        return pageManager.getTemplate(templatePath);
    }

    @Override
    public Collection<Template> getTemplates(final String parentPath) {
        return pageManager.getTemplates(parentPath);
    }

    @Override
    public Resource move(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict, final String[] adjustRefs) throws WCMException {
        return pageManager.move(resource, destination, beforeName, shallow, resolveConflict, adjustRefs);
    }

    @Override
    public void order(final Page page, final String beforeName) throws WCMException {
        pageManager.order(page, beforeName);
    }

    @Override
    public void order(final Page page, final String beforeName, final boolean autoSave) throws WCMException {
        pageManager.order(page, beforeName, autoSave);
    }

    @Override
    public void order(final Resource resource, final String beforeName) throws WCMException {
        pageManager.order(resource, beforeName);
    }

    @Override
    public void order(final Resource resource, final String beforeName, final boolean autoSave) throws WCMException {
        pageManager.order(resource, beforeName, autoSave);
    }

    @Override
    public void touch(final Node page, final boolean shallow, final Calendar now, final boolean clearRepl)
        throws WCMException {
        pageManager.touch(page, shallow, now, clearRepl);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final Calendar cal,
        final boolean includeNoLocal) throws WCMException {
        return pageManager.getChildRevisions(parentPath, cal, includeNoLocal);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final String treeRoot, final Calendar cal)
        throws WCMException {
        return pageManager.getChildRevisions(parentPath, treeRoot, cal);
    }

    @Override
    public Collection<Revision> getRevisions(final String path, final Calendar cal, final boolean includeNoLocal)
        throws WCMException {
        return pageManager.getRevisions(path, cal, includeNoLocal);
    }

    @Override
    public PageDecorator restoreTree(final String path, final Calendar date, final boolean preserveNV)
        throws WCMException {
        return decoratePage(pageManager.restoreTree(path, date, preserveNV));
    }

    // internals

    private PageDecorator decoratePage(final String path) {
        return decoratePage(pageManager.getPage(path));
    }

    private PageDecorator decoratePage(final Page page) {
        return page == null ? null : new DefaultPageDecorator(page);
    }
}
