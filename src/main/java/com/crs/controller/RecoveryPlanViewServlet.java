package com.crs.controller;

import com.crs.ejb.RecoveryService;
import com.crs.model.RecoveryPlanDetail;
import com.crs.model.StudentPlanMilestone;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/recovery-plan-view")
public class RecoveryPlanViewServlet extends HttpServlet {
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

        String studentPlanIdStr = request.getParameter("studentPlanId");
        if (studentPlanIdStr == null || studentPlanIdStr.isBlank()) {
            response.sendRedirect("recovery-plans");
            return;
        }

        int studentPlanId = Integer.parseInt(studentPlanIdStr);

        RecoveryPlanDetail planDetail = recoveryService.getRecoveryPlanDetailById(studentPlanId);
        List<StudentPlanMilestone> milestones = recoveryService.getMilestonesByPlanId(studentPlanId);

        if (planDetail == null) {
            response.sendRedirect("recovery-plans");
            return;
        }

        request.setAttribute("planDetail", planDetail);
        request.setAttribute("milestones", milestones);

        request.setAttribute("pageTitle", "Recovery Plan View");
        request.setAttribute("breadcrumb1", "Academic Management");
        request.setAttribute("breadcrumb2", "Recovery Plans");
        request.setAttribute("breadcrumb3", "View");
        request.setAttribute("currentPage", "recovery-workspace");

        request.getRequestDispatcher("/recoveryPlanView.jsp").forward(request, response);
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
        String studentPlanIdStr = request.getParameter("studentPlanId");

        if (userId == null || studentPlanIdStr == null || studentPlanIdStr.isBlank()) {
            response.sendRedirect("recovery-plans");
            return;
        }

        int studentPlanId = Integer.parseInt(studentPlanIdStr);

        // remove plan 不需要 milestoneId
        if ("removePlan".equals(action)) {
            recoveryService.removeRecoveryPlan(studentPlanId);
            request.getSession().setAttribute("successMessage", "Recovery plan removed successfully.");
            response.sendRedirect("recovery-plans");
            return;
        }

        // submit recovery result 也不需要 milestoneId
        if ("submitRecoveryResult".equals(action)) {
            String resultMarkStr = request.getParameter("resultMark");

            if (resultMarkStr != null && !resultMarkStr.isBlank()) {
                double resultMark = Double.parseDouble(resultMarkStr);
                recoveryService.completeRecoveryPlanWithResult(studentPlanId, resultMark, userId);

                request.getSession().setAttribute("successMessage", "Recovery result submitted successfully.");
            }

            response.sendRedirect("recovery-plan-view?studentPlanId=" + studentPlanId);
            return;
        }

        // 只有下面两个动作才需要 milestoneId
        String milestoneIdStr = request.getParameter("milestoneId");
        if (milestoneIdStr == null || milestoneIdStr.isBlank()) {
            response.sendRedirect("recovery-plan-view?studentPlanId=" + studentPlanId);
            return;
        }

        int milestoneId = Integer.parseInt(milestoneIdStr);

        if ("updateMilestone".equals(action)) {
            String description = request.getParameter("milestoneDescription");
            String durationStr = request.getParameter("durationDays");

            if (description != null && !description.isBlank() &&
                durationStr != null && !durationStr.isBlank()) {
                int durationDays = Integer.parseInt(durationStr);
                recoveryService.updateMilestone(studentPlanId, milestoneId, description.trim(), durationDays);

                request.getSession().setAttribute("successMessage", "Milestone updated successfully.");
            }

            response.sendRedirect("recovery-plan-view?studentPlanId=" + studentPlanId);
            return;
        }

        if ("completeMilestone".equals(action)) {
            recoveryService.completeMilestone(studentPlanId, milestoneId, userId);

            request.getSession().setAttribute("successMessage", "Milestone marked as completed.");
            response.sendRedirect("recovery-plan-view?studentPlanId=" + studentPlanId);
            return;
        }

        doGet(request, response);
    }
}