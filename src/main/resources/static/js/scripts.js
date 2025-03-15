document.addEventListener("DOMContentLoaded", function () {
    console.log("All events hooks here !!!");

    const sendOtpBtn = document.getElementById('send-otp-btn');
    const verifyOtpBtn = document.getElementById('verify-otp-btn');

    if (sendOtpBtn) {
        sendOtpBtn.addEventListener('click', function () {
            const emailField = document.getElementById('email');
            if (!emailField) {
                console.error('Email input field not found.');
                return;
            }

            const email = emailField.value;
            const button = sendOtpBtn;
            button.disabled = true; // Disable the button
            button.textContent = "Sending..."; // Feedback to the user

            // Call backend API to send OTP
            fetch('/auth/send-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ email })
            })
                .then(response => response.text())
                .then(responseMessage => {
                    button.disabled = false;
                    button.textContent = "Send OTP";

                    const statusElement = document.getElementById('status');
                    if (statusElement) {
                        statusElement.textContent = responseMessage;
                    }

                    if (responseMessage.includes("OTP sent")) {
                        const otpRequestSection = document.getElementById('otp-request-section');
                        const otpVerifySection = document.getElementById('otp-verify-section');
                        if (otpRequestSection && otpVerifySection) {
                            otpRequestSection.style.display = 'none';
                            otpVerifySection.style.display = 'block';
                        }
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    button.textContent = "Send OTP";
                    console.error('Error:', error);
                });
        });
    }

    if (verifyOtpBtn) {
        verifyOtpBtn.addEventListener('click', function () {
            const emailField = document.getElementById('email');
            const otpField = document.getElementById('otp');

            if (!emailField || !otpField) {
                console.error('Email or OTP input field not found.');
                return;
            }

            const email = emailField.value;
            const otp = otpField.value;
            const button = verifyOtpBtn;

            button.disabled = true; // Disable the button
            button.textContent = "Verifying..."; // Feedback to the user

            // Call backend API to verify OTP
            fetch('/auth/verify-otp', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: new URLSearchParams({ email, otpCode: otp })
            })
                .then(response => {
                    button.disabled = false;
                    button.textContent = "Verify OTP";

                    if (response.redirected) {
                        window.location.href = response.url; // Handle backend redirects
                    } else {
                        return response.text().then(text => {
                            // Display validation errors
                            console.error("Error:", text);
                        });
                    }
                })
                .catch(error => {
                    button.disabled = false;
                    button.textContent = "Verify OTP";
                    console.error('Error:', error);
                });
        });
    }
});

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> INSPECTOR <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    //  **********************************   Inspector - Form JS - Start   **********************************

    // Function to add a certificate row
    function addCertificate(tableBodyId) {
     const tableBody = document.getElementById(tableBodyId);
     const rowCount = tableBody.children.length;
     const newRow = document.createElement("tr");
     newRow.innerHTML = `
         <td><input type="text" name="certificates[${rowCount}].name" class="form-control" placeholder="Certificate Name"/></td>
         <td><input type="date" name="certificates[${rowCount}].dateIssued" class="form-control"/></td>
         <td><input type="date" name="certificates[${rowCount}].expiryDate" class="form-control"/></td>
         <td><input type="text" name="certificates[${rowCount}].issuer" class="form-control" placeholder="Issuer"/></td>
         <td><button type="button" class="btn btn-danger" onclick="removeCertificateRow(this)"><i class="fas fa-trash"></i></button></td>
     `;
     tableBody.appendChild(newRow);
    }

     // Function to remove a certificate row
     function removeCertificateRow(button) {
         const row = button.closest("tr");
         row.remove();
     }

     // Form validation
     (function () {
         'use strict'

         // Fetch all forms we want to apply validation to
         var forms = document.querySelectorAll('.needs-validation')

         // Loop over them and prevent submission
         Array.prototype.slice.call(forms)
             .forEach(function (form) {
                 form.addEventListener('submit', function (event) {
                     if (!form.checkValidity()) {
                         event.preventDefault()
                         event.stopPropagation()
                     }

                     form.classList.add('was-validated')
                 }, false)
             })
     })()

     //  **********************************   Inspector - Form JS - End   **********************************

     //  **********************************   Inspector - Management JS - Start   **********************************

     function openInspectorForm() {
         window.location.href = '/inspectors/form';
     }

     function redirectToEditForm(inspectorId) {
         window.location.href = '/inspectors/edit/' + inspectorId;
     }

     function redirectToViewForm(inspectorId) {
         window.location.href = '/inspectors/view/' + inspectorId;
     }

     // Initialize DataTable with responsive extension
     $(document).ready(function () {
         var table = $('#inspectorTable, #inspectionsTable').DataTable({
             "paging": true,
             "searching": true,
             "ordering": true,
             "info": true,
             "lengthMenu": [5, 10, 25, 50],
             "pageLength": 10,
             "language": {
                 "search": "Search: ",
                 "lengthMenu": "Show _MENU_ entries",
                 "info": "Showing _START_ to _END_ of _TOTAL_ entries"
             }
         });
     });

     //  **********************************   Inspector - Management JS - End   **********************************

 // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> INSPECTOR <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


 // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> INSPECTION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

     //  **********************************   Inspection - Management JS - End   **********************************

     function redirectToNewInspection() {
         window.location.href = '/inspection/new';
     }


     function redirectToEditInspection(inspectionId) {
         window.location.href = '/inspection/edit/' + inspectionId;
     }


     function redirectToViewInspection(inspectionId) {
         window.location.href = '/inspection/view/' + inspectionId;
     }
 // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> INSPECTION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LOGIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    function disableButton() {

    }
     document.getElementById('send-otp-btn').addEventListener('click', function () {
        const email = document.getElementById('email').value;

        const button = document.getElementById('send-otp-btn');
        button.disabled = true; // Disable the button
        button.textContent = "Sending..."; // Feedback to the user

        // Call backend API to send OTP
        fetch('/auth/send-otp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ email })
        })
        .then(response => response.text())
        .then(responseMessage => {
            document.getElementById('status').textContent = responseMessage;

            if (responseMessage.includes("OTP sent")) {
                // Show the verify OTP section
                document.getElementById('otp-request-section').style.display = 'none';
                document.getElementById('otp-verify-section').style.display = 'block';
            }
        })
        .catch(error => console.error('Error:', error));
    });

     document.getElementById('verify-otp-btn').addEventListener('click', function () {
        const button = document.getElementById('verify-otp-btn');
        button.disabled = true; // Disable the button
        button.textContent = "Verifying..."; // Feedback to the user

        const email = document.getElementById('email').value;
        const otp = document.getElementById('otp').value;

        // Call backend API to verify OTP
        fetch('/auth/verify-otp', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ email, otpCode: otp })
        })
        .then(response => {
            if (response.redirected) {
                credentials: 'include'
                window.location.href = response.url; // Handle backend redirects
            } else {
                return response.text().then(text => {
                    // Display validation errors
                    console.error("Error:", text);
                });
            }
        })

        .catch(error => console.error('Error:', error));
    });

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LOGIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<