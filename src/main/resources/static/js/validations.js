  /** ================= common-validation Section ================= **/

// Form validation
 (function() {
        'use strict';
        window.addEventListener('load', function() {
            // Fetch all the forms we want to apply custom Bootstrap validation styles to
            var forms = document.getElementsByClassName('needs-validation');
            // Loop over them and prevent submission
            var validation = Array.prototype.filter.call(forms, function(form) {
                form.addEventListener('submit', function(event) {
                    if (form.checkValidity() === false) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    form.classList.add('was-validated');
                }, false);
            });
        }, false);
    })();

// Add this to your existing validation setup
function setupDatePickerValidation() {
    const datePickerInput = document.getElementById('inspectionDateAsPerNotification');

    if (datePickerInput) {
        // Listen for changes on the date picker
        datePickerInput.addEventListener('change', function() {
            validateDatePicker();
        });

        // Also validate when form is submitted
        const form = datePickerInput.closest('form');
        if (form) {
            form.addEventListener('submit', function() {
                validateDatePicker();
            });
        }
    }
}

// Function to validate the date picker
function validateDatePicker() {
    const datePickerInput = document.getElementById('inspectionDateAsPerNotification');
    const feedbackElement = datePickerInput.nextElementSibling;

    if (datePickerInput && feedbackElement && feedbackElement.classList.contains('invalid-feedback')) {
        if (!datePickerInput.value) {
            datePickerInput.setCustomValidity('Please select inspection dates.');
            datePickerInput.classList.add('is-invalid');
        } else {
            datePickerInput.setCustomValidity('');
            datePickerInput.classList.remove('is-invalid');
        }
    }
}

// Function to validate international phone numbers more thoroughly
function validatePhoneNumber(phone) {
    // Basic pattern for international phone numbers
    const pattern = /^(\+?\d{1,3}[- ]?)?\d{6,14}$/;
    return pattern.test(phone);
}

// Phone number validation setup
function setupPhoneValidation() {
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            if (!validatePhoneNumber(this.value)) {
                this.setCustomValidity('Please enter a valid international phone number (6-14 digits, optional country code)');
            } else {
                this.setCustomValidity('');
            }
        });
    });
}

// Date validation for certificates
function setupCertificateDateValidation() {
    const today = new Date().toISOString().split('T')[0];
    document.querySelectorAll('.issue-date').forEach(input => {
        input.max = today;
    });

    // Initialize date validation for existing certificates
    document.querySelectorAll('.issue-date, .expiry-date').forEach(input => {
        validateCertificateDates(input);
    });
}

// Function to validate certificate dates
function validateCertificateDates(input) {
    const row = input.closest('tr');
    const issueDateInput = row.querySelector('.issue-date');
    const expiryDateInput = row.querySelector('.expiry-date');

    // Set max date for issue date (today)
    const today = new Date().toISOString().split('T')[0];
    if (issueDateInput) issueDateInput.max = today;

    if (issueDateInput && issueDateInput.value && expiryDateInput && expiryDateInput.value) {
        const issueDate = new Date(issueDateInput.value);
        const expiryDate = new Date(expiryDateInput.value);

        if (expiryDate < issueDate) {
            expiryDateInput.setCustomValidity('Expiry date must be after issue date');
            expiryDateInput.reportValidity();
        } else {
            expiryDateInput.setCustomValidity('');
        }

        // Update min date for expiry date
        expiryDateInput.min = issueDateInput.value;
    }
}

// Function to validate radio buttons
function setupRadioValidation() {
    document.querySelectorAll('.needs-validation').forEach(form => {
        // On submit validation
        form.addEventListener('submit', function (e) {
            let allRadiosValid = true;

            form.querySelectorAll('[role="radiogroup"]').forEach(group => {
                const name = group.querySelector('input[type="radio"]')?.name;
                const checked = form.querySelector(`input[name="${name}"]:checked`);
                if (!name || !checked) {
                    group.classList.add('is-invalid');
                    allRadiosValid = false;
                } else {
                    group.classList.remove('is-invalid');
                }
            });

            if (!allRadiosValid) {
                e.preventDefault();
                e.stopPropagation();
            }
        });

        // On change: clear error when one is selected
        form.querySelectorAll('[role="radiogroup"] input[type="radio"]').forEach(radio => {
            radio.addEventListener('change', function () {
                const group = this.closest('[role="radiogroup"]');
                if (group) {
                    group.classList.remove('is-invalid');
                }
            });
        });
    });
}

// If tech coord then match email while adding inspector
function handleInspectorTypeChange(radio) {
    if (radio.value === 'TECHNICAL_COORDINATOR') {
        showNotification('Important: For Technical Coordinators, the email must exactly match the employee records', 'warning');
    }
}

// Initialize all common validations
document.addEventListener('DOMContentLoaded', function() {
    setupRadioValidation();
    setupPhoneValidation();
    setupCertificateDateValidation();
    setupDatePickerValidation();
});

function setupInspectionStatusValidations(event) {
    const data = gatherInspectionData();
    const currentStatus = document.getElementById('inspectionStatus').value;

     if (handleRejectionOrClosure(event, currentStatus)) {
             return false;
         }

    // Define all status checks with requirements
   const STATUS_CHECKS = getStatusChecksConfig();

    // Complete workflow order
    const workflowOrder = getWorkflowOrder();

    const currentIndex = workflowOrder.indexOf(currentStatus);

    // 1. First validate all critical previous steps
     if (!validatePreviousCriticalSteps(event, currentStatus, STATUS_CHECKS, workflowOrder)) {
            return false;
        }

     // 2. Handle current status
      return handleCurrentStatus(event, currentStatus, STATUS_CHECKS);
}

// Helper functions
function handleRejectionOrClosure(event, currentStatus) {
    if (currentStatus === 'INSPECTION_REJECTED' || currentStatus === 'CLOSED') {
        event.preventDefault(); // Prevent default submission initially

        const isReject = currentStatus === 'INSPECTION_REJECTED';
        const message = isReject
            ? 'Are you sure you want to reject this inspection?'
            : 'Are you sure you want to close this inspection?';

        if (confirm(message)) {
            const form = event.target.closest('form'); // Find the closest parent form
            if (form) {
                form.submit(); // Submit the form programmatically
            } else {
                console.error("No form found to submit");
            }
            return true;
        }
        return false; // Do nothing if the user cancels
    }
    return false; // Return false if the status doesn't match
}

function getStatusChecksConfig() {
    return {
        'INSPECTOR_ASSIGNED': {
            check: (data) => {
                const hasInspectors = data.proposedCVs.length > 0;
                const hasTechCoords = data.proposedCVs.every(cv => cv.cvReviewByTechnicalCoordinator);
                return hasInspectors && hasTechCoords;
            },
            message: (data) => {
                const hasInspectors = data.proposedCVs.length > 0;
                const hasTechCoords = data.proposedCVs.every(cv => cv.cvReviewByTechnicalCoordinator);
                if (!hasInspectors) {
                    return "Please ensure that at least one inspector is assigned in the CV table before proceeding.";
                }
                if (!hasTechCoords) {
                    return "Each inspector must have a Technical Coordinator assigned prior to moving forward.";
                }
                return "";
            },
            isCritical: true
        },

        'INSPECTOR_REVIEW_AWAITING': {
            check: (data) => data.proposedCVs.every(cv => cv.cvReviewByTechnicalCoordinator),
            message: "All inspectors must have a Technical Coordinator assigned before proceeding.",
            isCritical: true
        },
        'INSPECTOR_APPROVED': {
            check: (data) => data.proposedCVs.some(cv =>
                cv.cvSubmittedToClientDate &&
                cv.cvStatus === true
            ),
            message: (data) => {
                const missingDate = data.proposedCVs.every(cv => !cv.cvSubmittedToClientDate);
                const notApproved = data.proposedCVs.every(cv => cv.cvStatus !== true);
                if (missingDate) {
                    return "Inspector(s) cv submitted to client date is missing. Please provide the date.";
                }
                 if (notApproved) {
                    return "Inspector(s) approval from client needs to update. Please update as approved.";
                }
                return "";
            },
            isCritical: true
        },
        'REFERENCE_DOC_RECEIVED': {
            check: (data) => data.referenceDocumentsForInspectionStatus && data.referenceDocumentsLink,
            message: (data) => {
                if (!data.referenceDocumentsForInspectionStatus) {
                    return "Please mark the reference documents as 'Available' before proceeding.";
                }
                if (!data.referenceDocumentsLink) {
                    return "The document link is missing. Please provide the document link.";
                }
                return "";
            },
            isCritical: true
        },
        'REFERENCE_DOC_REVIEW_AWAITING': {
            check: (data) => data.documentsReviewedByTechnicalCoordinator,
            message: "A Technical Coordinator must present to complete the document review.",
            isCritical: true
        },
        'INSPECTION_REPORTS_RECEIVED': {
            check: (data) => true,
            message: "Please provide the date when the inspection reports were received.",
            isCritical: false
        },
     'INSPECTION_REPORTS_REVIEW_AWAITING': {
         check: (data) => {
             return data.inspectionReports?.length > 0 && data.inspectionReports.every(report =>
                 report.reportDate &&
                 report.reportNumber &&
                 report.inspectorName &&
                 report.reportType &&
                 report.technicalCoordinator &&
                 report.reportLink
             );
         },
         message: (data) => {
             if (!data.inspectionReports?.length) {
                 return "At least one inspection report is required.";
             }

             const missingFields = [];

             data.inspectionReports.forEach((report, index) => {
                 const reportIssues = [];
                 if (!report.reportDate) reportIssues.push("Report Date");
                 if (!report.reportNumber) reportIssues.push("Report Number");
                 if (!report.inspectorName) reportIssues.push("Inspector Name");
                 if (!report.reportType) reportIssues.push("Report Type");
                 if (!report.technicalCoordinator) reportIssues.push("Technical Coordinator");
                 if (!report.reportLink) reportIssues.push("Report Link");

                 if (reportIssues.length) {
                     missingFields.push(`Report ${index + 1}: ${reportIssues.join(', ')}`);
                 }
             });

             return missingFields.length
                 ? `Please fill in all required fields for each report:\n\n${missingFields.join('\n')}`
                 : "A Technical Coordinator must present to review the inspection reports.";
         },
         isCritical: true
     },

    'INSPECTION_REPORTS_SENT_TO_CLIENT': {
        check: (data) => {
            return data.inspectionReports?.length > 0 &&
                data.inspectionReports.every(report =>
                    report.sentToClientDate &&
                    report.technicalCoordinatorRemarks
                );
        },
        message: (data) => {
            if (!data.inspectionReports?.length) {
                return "No inspection reports found. Please add at least one.";
            }

            const issues = [];

            data.inspectionReports.forEach((report, index) => {
                const missing = [];
                if (!report.sentToClientDate) missing.push("Sent to Client Date");
                if (!report.technicalCoordinatorRemarks) missing.push("Technical Coordinator Remarks");

                if (missing.length) {
                    issues.push(`Report ${index + 1}: Missing ${missing.join(", ")}`);
                }
            });

            return issues.length
                ? `Please fill the following fields before proceeding:\n\n${issues.join('\n')}`
                : "Each inspection report must have both Sent to Client Date and Technical Coordinator Remarks.";
        },
        isCritical: true
    },
        'INSPECTION_AWARDED': {
            check: (data) => data.jobFolderLink,
            message: "The job folder link is required to proceed with the inspection.",
            successMessage: "The inspection has been successfully awarded. Great job!",
            isCritical: true
        },
        'INSPECTOR_REVIEW_COMPLETED': {
            message: "Inspector CVs have been reviewed. Please share them with the client and update all necessary records.",
            isCritical: false
        },
        'REFERENCE_DOC_REVIEW_COMPLETED': {
            message: "Please verify that the contract review and inspection advice fields are updated.",
            isCritical: false
        },
        'INSPECTION_REPORTS_REVIEW_COMPLETED': {
            message: "The inspection report review has been completed. Prepare the reports for client submission.",
            isCritical: false
        }
    };
}

function getWorkflowOrder() {
    return [
        'NEW',
        'INSPECTOR_ASSIGNED',
        'INSPECTOR_REVIEW_AWAITING',
        'INSPECTOR_REVIEW_COMPLETED',
        'INSPECTOR_APPROVED',
        'REFERENCE_DOC_RECEIVED',
        'REFERENCE_DOC_REVIEW_AWAITING',
        'REFERENCE_DOC_REVIEW_COMPLETED',
        'INSPECTION_REPORTS_RECEIVED',
        'INSPECTION_REPORTS_REVIEW_AWAITING',
        'INSPECTION_REPORTS_REVIEW_COMPLETED',
        'INSPECTION_REPORTS_SENT_TO_CLIENT',
        'INSPECTION_AWARDED'
    ];
}

function gatherInspectionData() {
    return {
        proposedCVs: Array.from(document.querySelectorAll('#proposedCVsTable tbody tr')).map(row => ({
            cvCertificatesAvailable: row.querySelector('select[name$=".cvCertificatesAvailable"]')?.value === 'true',
            cvReviewByTechnicalCoordinator: row.querySelector('select[name$=".cvReviewByTechnicalCoordinator.empId"]')?.value,
            cvSubmittedToClientDate: row.querySelector('input[name$=".cvSubmittedToClientDate"]')?.value,
            cvStatus: row.querySelector('select[name$=".cvStatus"]')?.value === 'true',
            inspector: {
                inspectorId: row.querySelector('select[name$=".inspector.inspectorId"]')?.value
            }
        })).filter(cv => cv.inspector.inspectorId),
        referenceDocumentsForInspectionStatus: document.querySelector('input[name="referenceDocumentsForInspectionStatus"]:checked')?.value === 'true',
        referenceDocumentsLink: document.getElementById('referenceDocumentsLink')?.value,
        documentsReviewedByTechnicalCoordinator: document.getElementById('documentsReviewedByTechnicalCoordinator')?.value,
        irnSentDate: document.getElementById('irnSentDate')?.value,
        jobFolderLink: document.getElementById('jobFolderLink')?.value,
        inspectionReports: Array.from(document.querySelectorAll('.inspection-report')).map(report => ({
            reportDate: report.querySelector('input[name$=".reportDate"]')?.value,
            reportNumber: report.querySelector('input[name$=".reportNumber"]')?.value?.trim(),
            inspectorName: report.querySelector('select[name$=".inspectorName"]')?.value,
            reportType: report.querySelector('select[name$=".reportType"]')?.value,
            technicalCoordinator: report.querySelector('select[name$=".technicalCoordinator.empId"]')?.value,
            reportLink: report.querySelector('input[name$=".reportLink"]')?.value?.trim(),
            sentToClientDate: report.querySelector('input[name$=".sentToClientDate"]')?.value,
            technicalCoordinatorRemarks: report.querySelector('textarea[name$=".technicalCoordinatorRemarks"]')?.value?.trim()
        }))
    };
}

function validatePreviousCriticalSteps(event, currentStatus, STATUS_CHECKS, workflowOrder) {
    const currentIndex = workflowOrder.indexOf(currentStatus);
    const data = gatherInspectionData();

    for (let i = 0; i < currentIndex; i++) {
        const status = workflowOrder[i];
        const check = STATUS_CHECKS[status];

        if (check?.isCritical && check.check && !check.check(data)) {
            const errorMessage = typeof check.message === 'function'
                ? check.message(data)
                : check.message;

            showError(`${errorMessage}\n\n(Complete "${formatStatusName(status)}" first)`);
            event.preventDefault();
            return false;
        }
    }
    return true;
}

function handleCurrentStatus(event, currentStatus, STATUS_CHECKS) {
    const data = gatherInspectionData();
    const currentCheck = STATUS_CHECKS[currentStatus];

    if (!currentCheck) return true;

    if (currentCheck.isCritical) {
        if (!currentCheck.check(data)) {
            const errorMessage = typeof currentCheck.message === 'function'
                ? currentCheck.message(data)
                : currentCheck.message;

            showError(`${errorMessage}\n\n(Cannot proceed to "${formatStatusName(currentStatus)}")`);
            event.preventDefault();
            return false;
        }

        if (currentStatus === 'INSPECTION_AWARDED' && currentCheck.successMessage) {
            showSuccess(currentCheck.successMessage);
        }
    } else {
        const infoMessage = typeof currentCheck.message === 'function'
            ? currentCheck.message(data)
            : currentCheck.message;

        showInfo(`Status set to "${formatStatusName(currentStatus)}":\n\n${infoMessage}`);
    }

    return true;
}

function formatStatusName(status) {
    return status.split('_').map(word =>
        word.charAt(0) + word.slice(1).toLowerCase()
    ).join(' ');
}

function showPopup({ iconType, title, message, buttonText = 'OK' }) {
    // Create a container for the popup
    const popup = document.createElement('div');
    popup.className = 'popup-container';

    // Icon section
    const icon = document.createElement('div');
    icon.className = `popup-icon ${iconType}`;
    popup.appendChild(icon);

    // Title section
    const popupTitle = document.createElement('h2');
    popupTitle.className = 'popup-title';
    popupTitle.innerText = title;
    popup.appendChild(popupTitle);

    // Message section
    const popupMessage = document.createElement('div');
    popupMessage.className = 'popup-message';
    popupMessage.innerHTML = message.replace(/\n/g, '<br>');
    popup.appendChild(popupMessage);

    // Button section
    const button = document.createElement('button');
    button.className = 'popup-button';
    button.innerText = buttonText;
    button.addEventListener('click', () => {
        document.body.removeChild(popup);
    });
    popup.appendChild(button);

    // Append popup to body and show it
    document.body.appendChild(popup);
}

// Function to show error
function showError(message) {
    showPopup({
        iconType: 'error',
        title: 'Validation Error',
        message: message,
        buttonText: 'OK'
    });
}

// Function to show info
function showInfo(message) {
    showPopup({
        iconType: 'info',
        title: 'Information',
        message: message,
        buttonText: 'Got it'
    });
}

// Function to show success
function showSuccess(message) {
    showPopup({
        iconType: 'success',
        title: 'Success',
        message: message,
        buttonText: 'OK'
    });
}

function validatePQR(selectElement) {
    const selectedValue = selectElement.value;
    const inspectorId = selectElement.closest('tr').querySelector('select[name$=".inspector.inspectorId"]').value;

    if (selectedValue === 'true' && inspectorId) {
        showLoadingState(selectElement);
        fetch(`/pqr/validate/${inspectorId}`)
            .then(response => response.text())
            .then(result => {
             hideLoadingState(selectElement);
                if (result === "true") {
                    showSuccess("PQR form is valid.");
                } else {
                    selectElement.value = "false";
                    showError("PQR form is not valid. Please check the details.");
                }
            })
            .catch(error => {
                hideLoadingState(selectElement);
                console.error('Error:', error);
                showError("An error occurred while validating the PQR form.");
            });
    }
}

function showLoadingState(selectElement) {
    const loadingSpinner = document.createElement('div');
    loadingSpinner.className = 'loading-spinner';
    loadingSpinner.innerHTML = '<i class="fas fa-spinner fa-spin"></i>';
    loadingSpinner.style.position = 'absolute';
    loadingSpinner.style.right = '5px';
    loadingSpinner.style.top = '50%';
    loadingSpinner.style.transform = 'translateY(-50%)';
    const container = selectElement.closest('.d-flex');
    container.style.position = 'relative';
    container.appendChild(loadingSpinner);
    selectElement.disabled = true;
}

function hideLoadingState(selectElement) {
    const container = selectElement.closest('.d-flex');
    const spinner = container.querySelector('.loading-spinner');
    if (spinner) {
        spinner.remove();
    }
    selectElement.disabled = false;
}





