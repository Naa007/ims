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

    const MANDATORY_FIELDS = {
        'INSPECTOR_ASSIGNED': ['#proposedCVsTable tbody tr .inspectorId'],
        'INSPECTOR_REVIEW_AWAITING': ['select[name$=".cvReviewByTechnicalCoordinator.empId"]'],
        'INSPECTOR_APPROVED': [
            '#proposedCVsTable tbody tr input[name$=".cvSubmittedToClientDate"]',
            '#proposedCVsTable tbody tr select[name$=".cvStatus"]:not([value=""])'
        ],
        'REFERENCE_DOC_RECEIVED': [
            'input[name="referenceDocumentsForInspectionStatus"]:checked',
            '#referenceDocumentsLink'
        ],
        'REFERENCE_DOC_REVIEW_AWAITING': ['#documentsReviewedByTechnicalCoordinator'],
        'INSPECTION_REPORTS_REVIEW_AWAITING': ['#documentsReviewedByTechnicalCoordinator'],
        'INSPECTION_REPORTS_SENT_TO_CLIENT': ['#irnSentDate'],
        'INSPECTION_REPORTS_RECEIVED': ['#inspectionReportsReceivedDate'],
        'INSPECTION_AWARDED': ['#jobFolderLink']
    };

    const selectedStatus = document.getElementById('inspectionStatus');
    if (!selectedStatus) {
        console.warn('Inspection status select element not found');
        return;
    }

    let allMandatoryFieldsValid = true;
    let missingFields = [];
    const currentStatus = selectedStatus.value;
    const statusKeys = Object.keys(MANDATORY_FIELDS);
    const currentStatusIndex = statusKeys.indexOf(currentStatus);

    if (currentStatusIndex === -1) {
        console.warn('Current status is not defined in MANDATORY_FIELDS');
        return;
    }

    // Validate all previous statuses up to the current status
    for (let i = 0; i <= currentStatusIndex; i++) {
        const status = statusKeys[i];

        if (MANDATORY_FIELDS[status]) {
            const invalidFields = findMissingMandatoryFields(MANDATORY_FIELDS[status]);
            if (invalidFields.length > 0) {
                allMandatoryFieldsValid = false;
                missingFields.push(...invalidFields);
            }
        }
    }

    // Validate current status' mandatory fields
    if (allMandatoryFieldsValid && MANDATORY_FIELDS[selectedStatus]) {
        const invalidFields = findMissingMandatoryFields(MANDATORY_FIELDS[selectedStatus]);
        if (invalidFields.length > 0) {
            allMandatoryFieldsValid = false;
            missingFields.push(...invalidFields);
        }
    }

    if (!allMandatoryFieldsValid && missingFields.length > 0) {
        showPopup(`The following mandatory fields are missing:\n${missingFields.join('\n')}`);
    }
}

function findMissingMandatoryFields(selectors) {
    let missingFields = [];
    selectors.forEach(selector => {
        const elements = document.querySelectorAll(selector);
        elements.forEach(el => {
            if (!el.value || el.value.trim() === '') {
                missingFields.push(getElementDescription(el));
            }
        });
    });
    return missingFields;
}

function getElementDescription(element) {
    const label = element.closest('label') || element.getAttribute('aria-label');
    if (label) return label.innerText || label.toString();
    if (element.name) return `Field: ${element.name}`;
    if (element.id) return `Field with ID: ${element.id}`;
    return 'Unnamed Field';
}

function showPopup(message) {
    alert(message);
    event.preventDefault();
}