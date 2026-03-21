package com.crs.controller;

import com.crs.ejb.EmailTemplateService;
import com.crs.model.EmailTemplate;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/email-templates")
public class EmailTemplatesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private EmailTemplateService emailTemplateService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String moduleFilter = trimToEmpty(request.getParameter("module"));
        String search = trimToEmpty(request.getParameter("search"));
        String selectedCode = trimToEmpty(request.getParameter("selectedCode"));

        List<String> moduleOptions = emailTemplateService.getModuleOptions();
        List<EmailTemplate> templateList = emailTemplateService.getTemplates(moduleFilter, search);
        EmailTemplate selectedTemplate = emailTemplateService.resolveSelectedTemplate(selectedCode, templateList);

        request.setAttribute("moduleOptions", moduleOptions);
        request.setAttribute("templateList", templateList);
        request.setAttribute("selectedTemplate", selectedTemplate);
        request.setAttribute("moduleFilter", moduleFilter);
        request.setAttribute("search", search);
        request.setAttribute("pageTitle", "Email Templates");
        request.setAttribute("breadcrumb1", "Reference");
        request.setAttribute("breadcrumb2", "Email Templates");
        request.setAttribute("currentPage", "email-templates");

        request.getRequestDispatcher("email-templates.jsp").forward(request, response);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
