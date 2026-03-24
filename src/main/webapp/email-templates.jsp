<%@ page import="java.util.List" %>
<%@ page import="com.crs.model.EmailTemplate" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%!
    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String displayModule(String value) {
        return value == null ? "-" : value.replace("_", " ");
    }
%>
<%
    request.setAttribute("pageTitle", "Email Templates");
    request.setAttribute("breadcrumb1", "Reference");
    request.setAttribute("breadcrumb2", "Email Templates");
    request.setAttribute("currentPage", "email-templates");
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <%
        List<String> moduleOptionsData = (List<String>) request.getAttribute("moduleOptions");
        List<EmailTemplate> templateListData = (List<EmailTemplate>) request.getAttribute("templateList");
        EmailTemplate selectedTemplateData = (EmailTemplate) request.getAttribute("selectedTemplate");

        String moduleFilterValue = request.getAttribute("moduleFilter") == null ? "" : String.valueOf(request.getAttribute("moduleFilter"));
        String searchValue = request.getAttribute("search") == null ? "" : String.valueOf(request.getAttribute("search"));
    %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <div class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
                <div class="page-stack">
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h3 class="card-title">Email Templates</h3>
                                <p class="card-description">Read-only master template reference. No save, enable, disable, or reset actions are available in the final version.</p>
                            </div>
                            <div class="readonly-banner">Read-only template reference</div>
                        </div>

                        <form action="email-templates" method="get" class="template-filter-row top-gap-16">
                            <div class="field-stack">
                                <label class="field-label-upper" for="module">Module</label>
                                <select class="select-input" id="module" name="module">
                                    <option value="">All Modules</option>
                                    <% if (moduleOptionsData != null) { %>
                                        <% for (String moduleOptionData : moduleOptionsData) { %>
                                            <option value="<%= moduleOptionData %>" <%= moduleOptionData.equals(moduleFilterValue) ? "selected" : "" %>>
                                                <%= displayModule(moduleOptionData) %>
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="search">Search Template</label>
                                <input class="text-input" id="search" name="search" type="text" placeholder="Search by template name, code, or trigger" value="<%= escapeHtml(searchValue) %>" />
                            </div>

                            <div class="action-button-row">
                                <button type="submit" class="btn btn-outline btn-compact">Apply</button>
                                <button type="button" class="btn btn-outline btn-compact" onclick="this.form.module.value=''; this.form.search.value=''; this.form.submit();">Reset</button>
                            </div>
                        </form>
                    </section>

                    <div class="split-layout">
                        <section class="card content-card content-card-tight">
                            <div class="card-title-row">
                                <div class="card-title-group">
                                    <h3 class="card-title">Template List</h3>
                                    <p class="card-description">Choose a scenario template to inspect the stored content and preview.</p>
                                </div>
                            </div>

                            <div class="template-list top-gap-16">
                                <% if (templateListData != null && !templateListData.isEmpty()) { %>
                                    <% for (EmailTemplate templateItemData : templateListData) { %>
                                        <form action="email-templates" method="get">
                                            <input type="hidden" name="module" value="<%= escapeHtml(moduleFilterValue) %>" />
                                            <input type="hidden" name="search" value="<%= escapeHtml(searchValue) %>" />
                                            <input type="hidden" name="selectedCode" value="<%= templateItemData.getTemplateCode() %>" />

                                            <button type="submit" class="template-item <%= selectedTemplateData != null && templateItemData.getTemplateCode().equals(selectedTemplateData.getTemplateCode()) ? "active" : "" %>">
                                                <div class="template-item-top">
                                                    <div>
                                                        <div class="template-name"><%= templateItemData.getTemplateName() %></div>
                                                        <div class="template-subtle top-gap-8"><%= templateItemData.getTemplateCode() %></div>
                                                    </div>
                                                    <span class="tag-badge <%= templateItemData.isActive() ? "success" : "warning" %>"><%= templateItemData.isActive() ? "Enabled" : "Disabled" %></span>
                                                </div>
                                                <div class="meta-text"><%= displayModule(templateItemData.getModule()) %></div>
                                                <div class="meta-text">Trigger: <%= templateItemData.getTriggerEvent() %></div>
                                            </button>
                                        </form>
                                    <% } %>
                                <% } else { %>
                                    <div class="empty-box">No templates match the current filters.</div>
                                <% } %>
                            </div>
                        </section>

                        <div class="page-stack">
                            <% if (selectedTemplateData != null) { %>
                                <section class="card content-card">
                                    <div class="card-title-row">
                                        <div class="card-title-group">
                                            <h3 class="card-title"><%= selectedTemplateData.getTemplateName() %></h3>
                                            <p class="card-description"><%= selectedTemplateData.getTemplateCode() %> • Trigger: <%= selectedTemplateData.getTriggerEvent() %></p>
                                        </div>
                                        <span class="tag-badge <%= selectedTemplateData.isActive() ? "success" : "warning" %>"><%= selectedTemplateData.isActive() ? "Enabled" : "Disabled" %></span>
                                    </div>

                                    <div class="info-grid-2 top-gap-16">
                                        <div class="info-box">
                                            <div class="info-box-label">Module</div>
                                            <div class="info-box-value"><%= displayModule(selectedTemplateData.getModule()) %></div>
                                        </div>
                                        <div class="info-box">
                                            <div class="info-box-label">Preview Type</div>
                                            <div class="info-box-value"><%= selectedTemplateData.isHtmlTemplate() ? "HTML template" : "Plain text template" %></div>
                                        </div>
                                        <div class="info-box">
                                            <div class="info-box-label">Last Updated</div>
                                            <div class="info-box-value"><%= selectedTemplateData.getUpdatedAtDisplay() %></div>
                                        </div>
                                        <div class="info-box">
                                            <div class="info-box-label">Updated By</div>
                                            <div class="info-box-value"><%= safeText(selectedTemplateData.getUpdatedByName()).isBlank() ? "System default" : selectedTemplateData.getUpdatedByName() %></div>
                                        </div>
                                    </div>

                                    <div class="field-stack top-gap-16">
                                        <label class="field-label-upper">Subject Template</label>
                                        <pre class="template-source-block"><%= escapeHtml(selectedTemplateData.getSubjectTemplate()) %></pre>
                                    </div>

                                    <div class="field-stack top-gap-16">
                                        <label class="field-label-upper">Body Template Source</label>
                                        <pre class="template-source-block template-source-large"><%= selectedTemplateData.getBodyTemplateEscaped() %></pre>
                                    </div>
                                </section>

                                <section class="card content-card">
                                    <div class="card-title-row">
                                        <div class="card-title-group">
                                            <h3 class="card-title">Available Placeholders</h3>
                                            <p class="card-description">These values are filled automatically from saved system data when the actual email is sent.</p>
                                        </div>
                                    </div>

                                    <div class="placeholder-panel top-gap-16">
                                        <% if (selectedTemplateData.getPlaceholders() != null && !selectedTemplateData.getPlaceholders().isEmpty()) { %>
                                            <% for (String placeholderData : selectedTemplateData.getPlaceholders()) { %>
                                                <span class="tag-badge neutral">{<%= placeholderData %>}</span>
                                            <% } %>
                                        <% } else { %>
                                            <div class="empty-box">No placeholders detected in this template.</div>
                                        <% } %>
                                    </div>
                                </section>

                                <section class="card content-card">
                                    <div class="card-title-row">
                                        <div class="card-title-group">
                                            <h3 class="card-title">Live Preview</h3>
                                            <p class="card-description">Preview uses sample data only. Real sending uses saved data at the final trigger point.</p>
                                        </div>
                                    </div>

                                    <div class="page-stack top-gap-16">
                                        <div class="preview-box soft">
                                            <div class="info-box-label">Preview Subject</div>
                                            <div class="preview-content"><%= selectedTemplateData.getPreviewSubject() %></div>
                                        </div>

                                        <div class="preview-box">
                                            <div class="info-box-label">Preview Body</div>
                                            <div class="template-preview-shell top-gap-8">
                                                <%= selectedTemplateData.getPreviewBodyHtml() %>
                                            </div>
                                        </div>
                                    </div>
                                </section>
                            <% } else { %>
                                <section class="card content-card">
                                    <div class="empty-box">No template is available to preview for the current filters.</div>
                                </section>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
