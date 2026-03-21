<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    request.setAttribute("pageTitle", "Eligibility & Enrolment");
    request.setAttribute("breadcrumb1", "Academic Management");
    request.setAttribute("breadcrumb2", "Eligibility & Enrolment");
    request.setAttribute("breadcrumb3", "");
    request.setAttribute("currentPage", "eligibility");
%>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
</head>
<body class="app-body">
    <%@ include file="/WEB-INF/jspf/auth-check.jspf" %>

    <div class="app-shell">
        <%@ include file="/WEB-INF/jspf/sidebar.jspf" %>

        <div class="app-main">
            <%@ include file="/WEB-INF/jspf/topbar.jspf" %>

            <div class="app-content">
                <div class="page-stack">

                    <!-- FILTERS -->
                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Eligibility Working Filters</h2>
                            <p class="card-description">
                                Filter students by programme, intake, year, semester, or student ID / name.
                            </p>
                        </div>

                        <div class="filter-grid-5" style="margin-top: 18px;">
                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityProgramme">Programme</label>
                                <select id="eligibilityProgramme" class="select-input">
                                    <option value="">All</option>
                                    <option>Diploma in Computer Science</option>
                                    <option>Diploma in IT</option>
                                    <option>Diploma in Software Engineering</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityIntake">Intake</label>
                                <select id="eligibilityIntake" class="select-input">
                                    <option value="">All</option>
                                    <option>May 2024</option>
                                    <option>May 2025</option>
                                    <option>November 2024</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilityYear">Year</label>
                                <select id="eligibilityYear" class="select-input">
                                    <option value="">All</option>
                                    <option>Year 1</option>
                                    <option>Year 2</option>
                                    <option>Year 3</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilitySemester">Semester</label>
                                <select id="eligibilitySemester" class="select-input">
                                    <option value="">All</option>
                                    <option>Semester 1</option>
                                    <option>Semester 2</option>
                                </select>
                            </div>

                            <div class="field-stack">
                                <label class="field-label-upper" for="eligibilitySearch">Student ID / Name</label>
                                <div class="inline-field-row">
                                    <input id="eligibilitySearch" type="text" class="text-input" placeholder="Search Student ID or Name" />
                                    <button type="button" class="btn btn-outline">Reset</button>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- SUMMARY -->
                    <div class="summary-grid-4">
                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Awaiting Check</div>
                                <div class="metric-value">6</div>
                            </div>
                            <div class="metric-icon">&#10003;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Eligible Pending Approval</div>
                                <div class="metric-value">3</div>
                            </div>
                            <div class="metric-icon">&#9673;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Recovery Queue</div>
                                <div class="metric-value">2</div>
                            </div>
                            <div class="metric-icon">&#9678;</div>
                        </section>

                        <section class="card metric-card">
                            <div>
                                <div class="metric-label">Processed Cases</div>
                                <div class="metric-value">4</div>
                            </div>
                            <div class="metric-icon">&#9635;</div>
                        </section>
                    </div>

                    <!-- AWAITING CHECK -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Awaiting Eligibility Check (6)</h2>
                                <p class="card-description">
                                    Click Check All Eligibility to evaluate all students in this section.
                                </p>
                            </div>
                            <button type="button" class="btn btn-primary">Check All Eligibility</button>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Programme</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Current Term</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>
                                            <div class="strong-cell">Alex Wong</div>
                                            <div class="template-subtle">TP067001</div>
                                        </td>
                                        <td>Diploma in IT</td>
                                        <td>2.18</td>
                                        <td>2</td>
                                        <td>Year 2 / Semester 1</td>
                                        <td><span class="tag-badge dark">Not Yet Checked</span></td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <div class="strong-cell">Melissa Lee</div>
                                            <div class="template-subtle">TP067002</div>
                                        </td>
                                        <td>Diploma in Software Engineering</td>
                                        <td>1.94</td>
                                        <td>4</td>
                                        <td>Year 2 / Semester 1</td>
                                        <td><span class="tag-badge dark">Not Yet Checked</span></td>
                                    </tr>
                                    <tr>
                                        <td colspan="6" class="empty-row">Prototype rows only. Backend integration comes next.</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- PENDING APPROVAL -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Eligible Pending Approval (3)</h2>
                                <p class="card-description">
                                    Academic Officer can approve individually or in bulk.
                                </p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline">Select All</button>
                                <button type="button" class="btn btn-outline">Clear All</button>
                                <button type="button" class="btn btn-primary">Approve Selected Eligible</button>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Select</th>
                                        <th>Student</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Reason</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input type="checkbox" class="table-checkbox" /></td>
                                        <td>
                                            <div class="strong-cell">Aaron Goh</div>
                                            <div class="template-subtle">TP067003</div>
                                        </td>
                                        <td>2.64</td>
                                        <td>0</td>
                                        <td class="template-subtle">Meets CGPA and failed-course criteria.</td>
                                        <td><button type="button" class="btn btn-primary btn-compact">Approve Enrolment</button></td>
                                    </tr>
                                    <tr>
                                        <td><input type="checkbox" class="table-checkbox" /></td>
                                        <td>
                                            <div class="strong-cell">Daniel Ho</div>
                                            <div class="template-subtle">TP067005</div>
                                        </td>
                                        <td>2.09</td>
                                        <td>1</td>
                                        <td class="template-subtle">Eligible for next enrolment with minor failed-course record.</td>
                                        <td><button type="button" class="btn btn-primary btn-compact">Approve Enrolment</button></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- RECOVERY QUEUE -->
                    <section class="card content-card">
                        <div class="card-title-row">
                            <div class="card-title-group">
                                <h2 class="card-title">Not Eligible / Recovery Queue (2)</h2>
                                <p class="card-description">
                                    Students here are already checked and found not eligible.
                                </p>
                            </div>
                            <div class="button-row">
                                <button type="button" class="btn btn-outline">Select All</button>
                                <button type="button" class="btn btn-outline">Clear All</button>
                                <button type="button" class="btn btn-primary">Send Selected to Recovery</button>
                            </div>
                        </div>

                        <div class="table-wrap">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Select</th>
                                        <th>Student</th>
                                        <th>CGPA</th>
                                        <th>Failed Courses</th>
                                        <th>Reason</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input type="checkbox" class="table-checkbox" /></td>
                                        <td>
                                            <div class="strong-cell">Melissa Lee</div>
                                            <div class="template-subtle">TP067002</div>
                                        </td>
                                        <td>1.94</td>
                                        <td>4</td>
                                        <td class="template-subtle">CGPA below 2.0 and failed courses more than 3.</td>
                                        <td><button type="button" class="btn btn-outline btn-compact">Send to Recovery</button></td>
                                    </tr>
                                    <tr>
                                        <td><input type="checkbox" class="table-checkbox" /></td>
                                        <td>
                                            <div class="strong-cell">Nur Aina</div>
                                            <div class="template-subtle">TP067004</div>
                                        </td>
                                        <td>1.78</td>
                                        <td>2</td>
                                        <td class="template-subtle">CGPA below 2.0.</td>
                                        <td><button type="button" class="btn btn-outline btn-compact">Send to Recovery</button></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </section>

                    <!-- PROCESSED -->
                    <section class="card content-card">
                        <div class="card-title-group">
                            <h2 class="card-title">Processed Cases (4)</h2>
                            <p class="card-description">
                                Approved cases and sent-to-recovery cases are moved here after final action.
                            </p>
                        </div>

                        <div class="table-wrap" style="margin-top: 18px;">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Programme</th>
                                        <th>Decision</th>
                                        <th>Reason</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>
                                            <div class="strong-cell">Yasmin Tan</div>
                                            <div class="template-subtle">TP067006</div>
                                        </td>
                                        <td>Diploma in Computer Science</td>
                                        <td><span class="tag-badge success">Approved</span></td>
                                        <td class="template-subtle">Enrolment approved after eligibility confirmation.</td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <div class="strong-cell">Melissa Lee</div>
                                            <div class="template-subtle">TP067002</div>
                                        </td>
                                        <td>Diploma in Software Engineering</td>
                                        <td><span class="tag-badge warning">Sent to Recovery</span></td>
                                        <td class="template-subtle">Routed to recovery workflow after not-eligible outcome.</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </section>

                </div>
            </div>
        </div>
    </div>
</body>
</html>