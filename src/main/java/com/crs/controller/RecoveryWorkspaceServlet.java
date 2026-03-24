package com.crs.controller;

import com.crs.ejb.RecoveryService;
import com.crs.model.RecoveryAttemptInfo;
import com.crs.model.RecoveryComponentRecord;
import com.crs.model.RecoveryWorkspaceStudent;
import com.crs.model.StudentPlanMilestone;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/recovery-workspace")
public class RecoveryWorkspaceServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @EJB
    private RecoveryService recoveryService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roleName = (String) request.getSession().getAttribute("roleName");
        if (!"Academic Officer".equalsIgnoreCase(roleName)) {
            response.sendRedirect("user-management");
            return;
        }

        String studentId = request.getParameter("studentId");
        String selectedComponentIdStr = request.getParameter("selectedComponentId");

        if (studentId == null || studentId.isBlank()) {
            response.sendRedirect("recovery-plans");
            return;
        }

        RecoveryWorkspaceStudent student = recoveryService.getWorkspaceStudent(studentId);
        List<RecoveryComponentRecord> componentList = recoveryService.getComponentResults(studentId);

        RecoveryComponentRecord selectedComponent = null;
        Integer selectedComponentId = null;

        if (selectedComponentIdStr != null && !selectedComponentIdStr.isBlank()) {
            selectedComponentId = Integer.parseInt(selectedComponentIdStr);

            for (RecoveryComponentRecord component : componentList) {
                if (component.getStudentCourseComponentId() == selectedComponentId) {
                    selectedComponent = component;
                    break;
                }
            }
        }

        String selectedPlanStatus = null;
        List<StudentPlanMilestone> savedMilestones = null;

        if (selectedComponent != null) {
            selectedPlanStatus = recoveryService.getPlanStatusByComponent(selectedComponent.getStudentCourseComponentId());
            savedMilestones = recoveryService.getMilestonesByComponent(selectedComponent.getStudentCourseComponentId());
        }

        request.setAttribute("workspaceStudent", student);
        request.setAttribute("componentList", componentList);
        request.setAttribute("selectedComponent", selectedComponent);
        request.setAttribute("selectedPlanStatus", selectedPlanStatus);
        request.setAttribute("savedMilestones", savedMilestones);

        request.setAttribute("pageTitle", "Student Recovery Workspace");
        request.setAttribute("breadcrumb1", "Academic Management");
        request.setAttribute("breadcrumb2", "Recovery Plans");
        request.setAttribute("breadcrumb3", "Workspace");
        request.setAttribute("currentPage", "recovery-workspace");

        request.getRequestDispatcher("/recoveryWorkspace.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roleName = (String) request.getSession().getAttribute("roleName");
        if (!"Academic Officer".equalsIgnoreCase(roleName)) {
            response.sendRedirect("user-management");
            return;
        }

        Integer userId = (Integer) request.getSession().getAttribute("userId");
        String action = request.getParameter("action");
        String studentId = request.getParameter("studentId");

        if (userId == null || studentId == null || studentId.isBlank()) {
            response.sendRedirect("recovery-plans");
            return;
        }

        // 第一次 recovery：從 attempt 1 開 attempt 2
        if ("startRecovery".equals(action)) {
            String studentCourseComponentIdStr = request.getParameter("studentCourseComponentId");

            if (studentCourseComponentIdStr != null && !studentCourseComponentIdStr.isBlank()) {
                int oldStudentCourseComponentId = Integer.parseInt(studentCourseComponentIdStr);

                Integer newStudentCourseComponentId =
                        recoveryService.prepareNewRecoveryAttemptComponent(oldStudentCourseComponentId);

                if (newStudentCourseComponentId != null) {
                    response.sendRedirect("recovery-workspace?studentId=" + studentId + "&selectedComponentId=" + newStudentCourseComponentId);
                    return;
                }
            }

            response.sendRedirect("recovery-workspace?studentId=" + studentId);
            return;
        }

        // 第二次 recovery：從 attempt 2 開 attempt 3
        if ("addNewPlan".equals(action)) {
            String studentCourseComponentIdStr = request.getParameter("studentCourseComponentId");

            if (studentCourseComponentIdStr != null && !studentCourseComponentIdStr.isBlank()) {
                int oldStudentCourseComponentId = Integer.parseInt(studentCourseComponentIdStr);

                RecoveryAttemptInfo info =
                        recoveryService.findAttemptInfoByStudentCourseComponentId(oldStudentCourseComponentId);

                // 只有 attempt 2 / 3 才能 addNewPlan
                if (info != null && info.getAttemptNo() > 1) {
                    Integer newStudentCourseComponentId =
                            recoveryService.prepareNewRecoveryAttemptComponent(oldStudentCourseComponentId);

                    if (newStudentCourseComponentId != null) {
                    	request.getSession().setAttribute("successMessage", "New recovery attempt prepared successfully.");
                        response.sendRedirect("recovery-workspace?studentId=" + studentId + "&selectedComponentId=" + newStudentCourseComponentId);
                        return;
                    }
                }
            }

            response.sendRedirect("recovery-workspace?studentId=" + studentId);
            return;
        }

        // Save Milestones
        String studentCourseIdStr = request.getParameter("studentCourseId");
        String studentCourseComponentIdStr = request.getParameter("studentCourseComponentId");

        if (studentCourseIdStr == null || studentCourseIdStr.isBlank() ||
            studentCourseComponentIdStr == null || studentCourseComponentIdStr.isBlank()) {
            response.sendRedirect("recovery-plans");
            return;
        }

        int studentCourseId = Integer.parseInt(studentCourseIdStr);
        int studentCourseComponentId = Integer.parseInt(studentCourseComponentIdStr);

        recoveryService.saveMilestones(
                studentCourseId,
                studentCourseComponentId,
                request.getParameterValues("milestoneDescription"),
                request.getParameterValues("milestoneDuration"),
                userId,
                false
        );

        request.getSession().setAttribute("successMessage", "Recovery plan saved successfully.");
        response.sendRedirect("recovery-workspace?studentId=" + studentId + "&selectedComponentId=" + studentCourseComponentId);
    }
}