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
        event.preventDefault();
        const isReject = currentStatus === 'INSPECTION_REJECTED';

        Swal.fire({
            title: isReject ? 'Confirm Rejection' : 'Confirm Closure',
            text: isReject
                ? 'Are you sure you want to reject this inspection?'
                : 'Are you sure you want to close this inspection?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: isReject ? 'Yes, reject it' : 'Yes, close it',
            cancelButtonText: 'No, cancel'
        }).then((result) => {
            if (result.isConfirmed) {
                event.target.submit();
            }
        });

        return true;
    }
    return false;
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
                    return "👷‍♂️ Heads up! You need to assign at least one inspector in the CV table before moving forward.";
                }
                if (!hasTechCoords) {
                    return "👋 Hello buddy,not so fast! Each inspector needs a Technical Coordinator before we can move forward";
                }
                return "";
            },
            isCritical: true
        },

        'INSPECTOR_REVIEW_AWAITING': {
            check: (data) => data.proposedCVs.every(cv => cv.cvReviewByTechnicalCoordinator),
            message: "📋 Oops! All inspectors need a Technical Coordinator assigned. Let's loop them in!",
            isCritical: true
        },
        'INSPECTOR_APPROVED': {
            check: (data) => data.proposedCVs.every(cv =>
                cv.cvSubmittedToClientDate &&
                cv.cvStatus === true
            ),
            message: (data) => {
                if (data.proposedCVs.some(cv => !cv.cvSubmittedToClientDate)) {
                    return "📅 Looks like some CVs are still waiting for a submission date. Let's not keep the client guessing! ⏳";
                }
                if (data.proposedCVs.some(cv => cv.cvStatus !== true)) {
                    return "✅ Approval alert! Some CVs still need the green signal. Give them the stamp of approval! 🟢";
                }
                return "";
            },
            isCritical: true
        },
        'REFERENCE_DOC_RECEIVED': {
            check: (data) => data.referenceDocumentsForInspectionStatus && data.referenceDocumentsLink,
            message: (data) => {
                if (!data.referenceDocumentsForInspectionStatus) return "📄 Heads up! Don't forget to mark the reference documents as 'Available'. We can't inspect thin air! 😉";
                if (!data.referenceDocumentsLink) return "🔗 Oops! The document link is missing. Even Sherlock Holmes needs a clue!";
                return "";
            },
            isCritical: true
        },
        'REFERENCE_DOC_REVIEW_AWAITING': {
            check: (data) => data.documentsReviewedByTechnicalCoordinator,
            message: "🧐 Technical Coordinator needed for document review! Let's call in the experts!",
            isCritical: true
        },
        'INSPECTION_REPORTS_RECEIVED': {
            check: (data) => data.inspectionReportsReceivedDate,
            message: "📅 Please provide the inspection reports received date.",
            isCritical: true
        },
        'INSPECTION_REPORTS_REVIEW_AWAITING': {
            check: (data) => data.documentsReviewedByTechnicalCoordinator,
            message: "🧠 Don't forget! Assign a Technical Coordinator to review the reports.",
            isCritical: true
        },
        'INSPECTION_REPORTS_SENT_TO_CLIENT': {
            check: (data) => data.irnSentDate,
            message: "📤 IRN sent date is missing! Let's not leave the client hanging",
            isCritical: true
        },
        'INSPECTION_AWARDED': {
            check: (data) => data.jobFolderLink,
            message: "🔗 Where's the job folder link? Job folder link must be provided",
            successMessage: "Mission accomplished🎯! The inspection is now Awarded. Let's keep the momentum going!",
            isCritical: true
        },
        'INSPECTOR_REVIEW_COMPLETED': {
            message: "📬 Time to hit send! Share those Inspector CVs with the client and don't forget to update the records. Good job so far! 🛠️",
            isCritical: false
        },
        'REFERENCE_DOC_REVIEW_COMPLETED': {
            message: "📋 Quick checklist! Make sure the contract review and inspection advice fields are updated🏁",
            isCritical: false
        },
        'INSPECTION_REPORTS_REVIEW_COMPLETED': {
            message: "🎯 Reports review completed! Time to prep for client submission like a pro.",
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
        inspectionReportsReceivedDate: document.getElementById('inspectionReportsReceivedDate')?.value,
        irnSentDate: document.getElementById('irnSentDate')?.value,
        jobFolderLink: document.getElementById('jobFolderLink')?.value
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

function showError(message) {
    Swal.fire({
        icon: 'error',
        title: 'Validation Required',
        html: message.replace(/\n/g, '<br>'),
        confirmButtonText: 'OK'
    });
}

function showInfo(message) {
    Swal.fire({
        icon: 'info',
        title: 'Status Update',
        html: message.replace(/\n/g, '<br>'),
        confirmButtonText: 'Got it'
    });
}

function showSuccess(message) {
    Swal.fire({
        icon: 'success',
        title: '🎉 Congratulations!',
        html: `
            ${message.replace(/\n/g, '<br>')}
            <br>
            <img src="https://media.giphy.com/media/l0MYEqEzwMWFCg8rm/giphy.gif" width="200" style="margin-top:15px;">
        `,
        confirmButtonText: 'Celebrate 🎊'
    });
}



