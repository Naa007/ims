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
            form.addEventListener('submit', function(e) {
                let allRadiosValid = true;

                // Validate all radio groups
                form.querySelectorAll('[role="radiogroup"]').forEach(group => {
                    const name = group.querySelector('input[type="radio"]')?.name;
                    if (!name || !form.querySelector(`input[name="${name}"]:checked`)) {
                        group.classList.add('is-invalid');
                        allRadiosValid = false;
                    }
                });

                if (!allRadiosValid) {
                    e.preventDefault();
                    e.stopPropagation();
                }
            });
        });
    }

    // Initialize all common validations
    document.addEventListener('DOMContentLoaded', function() {
        setupRadioValidation();
        setupPhoneValidation();
        setupCertificateDateValidation();
    });