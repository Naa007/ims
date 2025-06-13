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

// Initialization
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

// Validation Functions
function validateStatusRequirements(status, inspection) {
    let isValid = true;
    let message = '';

    switch(status) {
        case 'INSPECTOR_ASSIGNED':
        const isTechCoOrdPresent = Array.from(document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]'))
                        .every(select => select.value && select.value !== "");
            if (inspection.proposedCVs.length === 0 ) {
                isValid = false;
                message = 'At least one inspector should be present in the CV';
            }
            if ( !isTechCoOrdPresent ) {
                 isValid = false;
                  message = 'At least one inspector and respective technical coordinator should be present in the CV';
                }
            break;

        case 'INSPECTOR_REVIEW_AWAITING':
            const allCVsHaveTechCoord = Array.from(document.querySelectorAll('select[name$=".cvReviewByTechnicalCoordinator.empId"]'))
                .every(select => select.value && select.value !== "");
            if (!allCVsHaveTechCoord) {
                isValid = false;
                message = 'Technical Coordinator should be assigned for all CVs';
            }
            break;

        case 'INSPECTOR_REVIEW_COMPLETED':
            isValid = true;
            message = 'Please send Inspector CV details to Client and update here';
            break;

        case 'INSPECTOR_APPROVED':
            if (inspection.proposedCVs.length === 0) {
                isValid = false;
                message = 'At least one CV must be provided before approving the inspector';
            } else {
                const invalidCVs = inspection.proposedCVs.filter(cv => {
                    return !cv.cvSubmittedToClientDate || !cv.cvStatus;
                });
                if (invalidCVs.length > 0) {
                    isValid = false;
                    message = 'CV Submitted Date should not be empty and CV Status should be "Approved"';
                }
            }
            break;

        case 'REFERENCE_DOC_RECEIVED':
            if (!inspection.referenceDocumentsForInspectionStatus || !inspection.referenceDocumentsLink) {
                isValid = false;
                message = 'Reference documents should be marked as available and link should be provided';
            }
            break;

        case 'REFERENCE_DOC_REVIEW_AWAITING':
            if (!inspection.documentsReviewedByTechnicalCoordinator) {
                isValid = false;
                message = 'Technical Coordinator should be assigned for document review';
            }
            break;

        case 'REFERENCE_DOC_REVIEW_COMPLETED':
            isValid = true;
            message = 'Hope contract review and inspection advise fields are up to date';
            break;

        case 'INSPECTION_REPORTS_SENT_TO_CLIENT':
            if (!inspection.irnSentDate) {
                isValid = false;
                message = 'IRN Sent Date should not be empty';
            }
            break;

        case 'INSPECTION_REPORTS_RECEIVED':
            if (!inspection.inspectionReportsReceivedDate) {
                isValid = false;
                message = 'Inspection reports received date should not be empty';
            }
            break;

        case 'INSPECTION_AWARDED':
            if (!inspection.jobFolderLink) {
                isValid = false;
                message = '🏗️ The job folder is missing. Let’s not celebrate just yet!';
            } else {
                isValid = true;
                message = '🎉 Congratulations!  The inspection has been *officially* awarded!';
            }
            break;

        case 'INSPECTION_REJECTED':
            isValid = true;
            message = 'Inspection rejected😢-don\'t worry - the next one will be a winner!';
            break;

        case 'CLOSED':
            isValid = true;
            message = 'Inspection closed successfully';
            break;
    }

    return { isValid, message };
}

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

// Helper Functions
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

function handleValidationError(status, message, form, inspection) {
    if (status === 'INSPECTION_AWARDED') {
        const validAward = validateStatusRequirements('INSPECTION_AWARDED', inspection).isValid;
        if (validAward) {
            showNotification(message, 'success');
        } else {
            showNotification(message, 'warning');
        }
        return;
    }

    if (status === 'CLOSED') {
        showNotification(message, 'success');
    } else if (status === 'INSPECTOR_REVIEW_COMPLETED' || status === 'REFERENCE_DOC_REVIEW_COMPLETED') {
        showNotification(message, 'warning');
    } else {
        showNotification(message, 'error');
    }
}


// Event Handler
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
        handleValidationError(status, validation.message, form, inspection);

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
        setupRadioValidation();
    });