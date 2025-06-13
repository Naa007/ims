  /** ================= common-validation Section ================= **/

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


    function handleInspectorTypeChange(radio) {
        if (radio.value === 'TECHNICAL_COORDINATOR') {
            showNotification('Important: For Technical Coordinators, the email must exactly match the employee records', 'warning');
        }
    }


const WORKFLOW_SEQUENCE = [
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
    'INSPECTION_AWARDED',
    'CLOSED'
];

const NO_RESET_STATUSES = [
    'INSPECTOR_REVIEW_COMPLETED',
    'REFERENCE_DOC_REVIEW_COMPLETED',
    'INSPECTION_REJECTED',
    'CLOSED'
];

// Initialize all validations when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    setupFormValidation();
    setupInspectionStatusValidation();
    setupDynamicValidations();
    setupDatePickerValidation();
});

// Main form validation setup
function setupFormValidation() {
    const form = document.querySelector('form.needs-validation');
    if (!form) return;

    form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
            highlightInvalidFields();
        }
        form.classList.add('was-validated');
    }, false);
}

// Status validation initialization
function setupInspectionStatusValidation() {
    const statusSelect = document.getElementById('inspectionStatus');
    if (!statusSelect) {
        console.warn('Inspection status select element not found');
        return;
    }

    const form = document.querySelector('form');
    if (!form) return;

    // Initialize previous status field
    if (!form.querySelector('input[name="previousStatus"]')) {
        const hiddenInput = document.createElement('input');
        hiddenInput.type = 'hidden';
        hiddenInput.name = 'previousStatus';
        hiddenInput.value = statusSelect.value;
        form.insertBefore(hiddenInput, form.firstChild);
    }

    // Initialize completed statuses tracking
    if (!form.querySelector('input[name="completedStatuses"]')) {
        const completedStatusesInput = document.createElement('input');
        completedStatusesInput.type = 'hidden';
        completedStatusesInput.name = 'completedStatuses';
        completedStatusesInput.value = statusSelect.value ? JSON.stringify([statusSelect.value]) : '[]';
        form.insertBefore(completedStatusesInput, form.firstChild);
    }

    statusSelect.addEventListener('change', handleStatusChange);
}

// Setup dynamic validations for various fields
function setupDynamicValidations() {
    // Radio button validations
    document.querySelectorAll('.form-check-input[type="radio"]').forEach(radio => {
        radio.addEventListener('change', function() {
            const groupName = this.name;
            const group = document.querySelectorAll(`input[name="${groupName}"]`);
            const isChecked = Array.from(group).some(r => r.checked);
            const container = this.closest('.input-group') || this.closest('.form-check');

            if (!isChecked) {
                showFieldError(container, 'This selection is required');
            } else {
                clearFieldError(container);
            }
        });
    });

    // Phone number validation
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        input.addEventListener('input', function() {
            const phonePattern = /^[0-9]{10,15}$/;
            if (!phonePattern.test(this.value)) {
                showFieldError(this, 'Please enter a valid phone number (10-15 digits)');
            } else {
                clearFieldError(this);
            }
        });
    });

    // URL validation
    document.querySelectorAll('input[type="url"]').forEach(input => {
        input.addEventListener('input', function() {
            try {
                new URL(this.value);
                clearFieldError(this);
            } catch (_) {
                showFieldError(this, 'Please enter a valid URL');
            }
        });
    });
}

// Date picker validation
function setupDatePickerValidation() {
    const datePicker = document.getElementById('inspectionDateAsPerNotification');
    if (datePicker) {
        datePicker.addEventListener('change', function() {
            if (!this.value) {
                showFieldError(this, 'Please select at least one date');
            } else {
                clearFieldError(this);
            }
        });
    }
}

// Show error message below a field
function showFieldError(field, message) {
    // Remove any existing error message
    clearFieldError(field);

    // Create error message element
    const errorElement = document.createElement('div');
    errorElement.className = 'invalid-feedback d-block';
    errorElement.textContent = message;

    // Add error class to field
    if (field.classList) {
        field.classList.add('is-invalid');
    } else {
        // For radio/checkbox groups
        const inputs = field.querySelectorAll('input');
        inputs.forEach(input => input.classList.add('is-invalid'));
    }

    // Insert error message after the field or its container
    if (field.parentNode && field.parentNode.classList.contains('input-group')) {
        field.parentNode.parentNode.appendChild(errorElement);
    } else {
        field.parentNode.appendChild(errorElement);
    }

    // Scroll to the field
    field.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

// Clear error message from a field
function clearFieldError(field) {
    // Remove error class
    if (field.classList) {
        field.classList.remove('is-invalid');
    } else {
        // For radio/checkbox groups
        const inputs = field.querySelectorAll('input');
        inputs.forEach(input => input.classList.remove('is-invalid'));
    }

    // Remove error message element
    const parent = field.parentNode;
    if (parent) {
        const errorElements = parent.querySelectorAll('.invalid-feedback.d-block');
        errorElements.forEach(el => el.remove());
    }
}

// Highlight invalid fields
function highlightInvalidFields(selector) {
    if (selector) {
        // Highlight specific fields
        document.querySelectorAll(selector).forEach(el => {
            el.classList.add('is-invalid');
            el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
    } else {
        // Highlight all invalid fields
        document.querySelectorAll('.form-control:invalid, .form-select:invalid').forEach(el => {
            el.classList.add('is-invalid');
        });
    }
}

// Gather inspection data from form
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
        inspectionReportsReceivedDate: document.getElementById('inspectionReportsReceivedDate')?.value
    };
}

// Validate status requirements
function validateStatusRequirements(status, inspection) {
    let isValid = true;
    let message = '';

    switch(status) {
        case 'INSPECTOR_ASSIGNED':
            // Clear previous validation states
            document.querySelectorAll('#proposedCVsTable select').forEach(select => {
                select.classList.remove('is-invalid');
            });

            let inspectorErrors = [];

            // 1. Validate at least one inspector is selected
            if (inspection.proposedCVs.length === 0) {
                isValid = false;
                inspectorErrors.push('At least one inspector should be present in the CV');
                document.querySelectorAll('#proposedCVsTable select[name$=".inspector.inspectorId"]')
                       .forEach(select => select.classList.add('is-invalid'));
            }

            // 2. Validate all CVs have technical coordinators
            const allHaveCoordinators = Array.from(document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]'))
                                       .every(select => select.value && select.value !== "");
            if (!allHaveCoordinators) {
                isValid = false;
                inspectorErrors.push('Technical Coordinator should be assigned for all CVs');
                document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]')
                       .forEach(select => select.classList.add('is-invalid'));
            }

            if (inspectorErrors.length > 0) {
                showNotification(inspectorErrors.join('\n'), 'error');

                // Focus first invalid field
                setTimeout(() => {
                    const firstInvalid = document.querySelector('.is-invalid');
                    if (firstInvalid) {
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        firstInvalid.focus();
                    }
                }, 100);
            }
            break;

        case 'INSPECTOR_REVIEW_AWAITING':
            document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]')
                   .forEach(select => select.classList.remove('is-invalid'));

            const coordinatorsValid = Array.from(document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]'))
                                      .every(select => select.value && select.value !== "");
            if (!coordinatorsValid) {
                isValid = false;
                message = 'Technical Coordinator should be assigned for all CVs';
                showNotification(message, 'error');
                document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]')
                       .forEach(select => select.classList.add('is-invalid'));
            }
            break;

        case 'INSPECTOR_REVIEW_COMPLETED':
            isValid = true;
            message = 'Please send Inspector CV details to Client and update here';
            showNotification(message, 'info');
            break;

        case 'INSPECTOR_APPROVED':
            document.querySelectorAll('#proposedCVsTable input, #proposedCVsTable select')
                   .forEach(field => field.classList.remove('is-invalid'));

            let approvalErrors = [];

            if (inspection.proposedCVs.length === 0) {
                isValid = false;
                approvalErrors.push('At least one CV must be provided before approving the inspector');
                document.getElementById('proposedCVsTable').classList.add('is-invalid');
            } else {
                const invalidCVs = inspection.proposedCVs.filter(cv => {
                    return !cv.cvSubmittedToClientDate || !cv.cvStatus;
                });
                if (invalidCVs.length > 0) {
                    isValid = false;
                    approvalErrors.push('CV Submitted Date should not be empty and CV Status should be "Approved"');
                    document.querySelectorAll('input[name$=".cvSubmittedToClientDate"], select[name$=".cvStatus"]')
                           .forEach(field => field.classList.add('is-invalid'));
                }
            }

            if (approvalErrors.length > 0) {
                showNotification(approvalErrors.join('\n'), 'error');
            }
            break;

        case 'REFERENCE_DOC_RECEIVED':
            // Clear previous validation
            document.querySelector('[name="referenceDocumentsForInspectionStatus"]').classList.remove('is-invalid');
            document.getElementById('referenceDocumentsLink').classList.remove('is-invalid');

            let docErrors = [];

            if (!inspection.referenceDocumentsForInspectionStatus) {
                docErrors.push('Reference documents should be marked as available');
                document.querySelector('[name="referenceDocumentsForInspectionStatus"]').classList.add('is-invalid');
            }

            if (!inspection.referenceDocumentsLink) {
                docErrors.push('Reference documents link should be provided');
                document.getElementById('referenceDocumentsLink').classList.add('is-invalid');
            }

            if (docErrors.length > 0) {
                isValid = false;
                showNotification(docErrors.join('\n'), 'error');

                setTimeout(() => {
                    const firstInvalid = document.querySelector('#referenceDocumentsLink.is-invalid, [name="referenceDocumentsForInspectionStatus"].is-invalid');
                    if (firstInvalid) {
                        firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        if (firstInvalid.tagName === 'INPUT') firstInvalid.focus();
                    }
                }, 100);
            }
            break;

        case 'REFERENCE_DOC_REVIEW_AWAITING':
            document.getElementById('documentsReviewedByTechnicalCoordinator').classList.remove('is-invalid');

            if (!inspection.documentsReviewedByTechnicalCoordinator) {
                isValid = false;
                message = 'Technical Coordinator should be assigned for document review';
                showNotification(message, 'error');
                document.getElementById('documentsReviewedByTechnicalCoordinator').classList.add('is-invalid');
            }
            break;

        case 'REFERENCE_DOC_REVIEW_COMPLETED':
            isValid = true;
            message = 'Hope contract review and inspection advise fields are up to date';
            showNotification(message, 'info');
            break;

        case 'INSPECTION_REPORTS_RECEIVED':
            document.getElementById('inspectionReportsReceivedDate').classList.remove('is-invalid');

            if (!inspection.inspectionReportsReceivedDate) {
                isValid = false;
                message = 'Inspection reports received date should not be empty';
                showNotification(message, 'error');
                document.getElementById('inspectionReportsReceivedDate').classList.add('is-invalid');
            }
            break;

        case 'INSPECTION_REPORTS_SENT_TO_CLIENT':
            document.getElementById('irnSentDate').classList.remove('is-invalid');

            if (!inspection.irnSentDate) {
                isValid = false;
                message = 'IRN Sent Date should not be empty';
                showNotification(message, 'error');
                document.getElementById('irnSentDate').classList.add('is-invalid');
            }
            break;

        case 'INSPECTION_AWARDED':
            document.getElementById('jobFolderLink').classList.remove('is-invalid');

            if (!inspection.jobFolderLink) {
                isValid = false;
                message = 'The job folder is missing';
                showNotification(message, 'error');
                document.getElementById('jobFolderLink').classList.add('is-invalid');
            } else {
                message = 'Congratulations! The inspection has been officially awarded!';
                showNotification(message, 'success');
            }
            break;

        case 'INSPECTION_REJECTED':
            isValid = true;
            message = 'Inspection rejected';
            showNotification(message, 'info');
            break;

        case 'CLOSED':
            isValid = true;
            message = 'Inspection closed successfully';
            showNotification(message, 'success');
            break;
    }

    return { isValid, message };
}

// Check if status is completed
function isStatusCompleted(status, inspection, completedStatuses) {
    if (completedStatuses.includes(status)) return true;

    if (status === 'INSPECTOR_REVIEW_COMPLETED') {
        return completedStatuses.includes('INSPECTOR_REVIEW_AWAITING') ||
               validateStatusRequirements('INSPECTOR_REVIEW_AWAITING', inspection).isValid;
    }

    if (status === 'REFERENCE_DOC_REVIEW_COMPLETED') {
        return completedStatuses.includes('REFERENCE_DOC_REVIEW_AWAITING') ||
               validateStatusRequirements('REFERENCE_DOC_REVIEW_AWAITING', inspection).isValid;
    }

    return validateStatusRequirements(status, inspection).isValid;
}

// Main inspection status validation
function validateInspectionStatus(status, inspection, completedStatuses = []) {
    if (status === 'CLOSED') {
        if (!completedStatuses.includes('INSPECTION_AWARDED')) {
            return { isValid: false, message: 'Cannot close inspection until it has been awarded' };
        }
        return { isValid: true, message: 'Inspection closed successfully' };
    }

    if (status === 'INSPECTION_REJECTED') {
        if (completedStatuses.includes('INSPECTION_AWARDED')) {
            return { isValid: false, message: 'Cannot reject an inspection that has been awarded' };
        }
        return { isValid: true, message: 'Inspection rejected successfully' };
    }

    const currentIndex = WORKFLOW_SEQUENCE.indexOf(status);
    if (currentIndex === -1) return { isValid: false, message: 'Invalid status' };

    for (let i = 0; i < currentIndex; i++) {
        const prevStatus = WORKFLOW_SEQUENCE[i];

        if ((status === 'INSPECTOR_APPROVED' && prevStatus === 'INSPECTOR_REVIEW_COMPLETED') ||
            (status === 'INSPECTION_REPORTS_SENT_TO_CLIENT' && prevStatus === 'REFERENCE_DOC_REVIEW_COMPLETED')) {
            const awaitingStatus = prevStatus.replace('COMPLETED', 'AWAITING');
            if (isStatusCompleted(awaitingStatus, inspection, completedStatuses)) continue;
        }

        if (!isStatusCompleted(prevStatus, inspection, completedStatuses)) {
            return { isValid: false, message: `Please complete ${prevStatus.replace(/_/g, ' ')} step first` };
        }
    }

    return validateStatusRequirements(status, inspection);
}

// Handle status change event
function handleStatusChange() {
    const status = this.value;
    const form = document.querySelector('form');
    if (!form) return;

    const previousStatusInput = form.querySelector('input[name="previousStatus"]');
    const completedStatusesInput = form.querySelector('input[name="completedStatuses"]');
    if (!previousStatusInput || !completedStatusesInput) return;

    const inspection = gatherInspectionData();
    let completedStatuses = [];

    try {
        completedStatuses = JSON.parse(completedStatusesInput.value || '[]');
    } catch (e) {
        console.error('Error parsing completed statuses:', e);
        completedStatuses = [];
    }

    const validation = validateInspectionStatus(status, inspection, completedStatuses);

    if (!validation.isValid) {
        // Errors are already shown in the UI via showFieldError()
        if (!NO_RESET_STATUSES.includes(status)) {
            this.value = previousStatusInput.value || '';
        }
    } else {
        // Update completed statuses
        if (status === 'INSPECTION_REJECTED') {
            const currentIndex = WORKFLOW_SEQUENCE.indexOf(status);
            completedStatuses = Array.from(new Set([
                ...completedStatuses,
                ...WORKFLOW_SEQUENCE.slice(0, currentIndex + 1)
            ]));
        } else if (!completedStatuses.includes(status)) {
            completedStatuses.push(status);
        }

        completedStatusesInput.value = JSON.stringify(completedStatuses);
        previousStatusInput.value = status;
    }
}

    // Initialize all common validations
    document.addEventListener('DOMContentLoaded', function() {
        setupRadioValidation();
        setupPhoneValidation();
        setupCertificateDateValidation();
        setupDatePickerValidation();
        setupInspectionStatusValidation();

    });