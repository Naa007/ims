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

     // Initialize Map with default or provided location
    const locationDetails = document.getElementById('inspectionLocationDetails');
    if (locationDetails) {
        const locationDetailsValue = locationDetails.value;
        initMap(locationDetailsValue ? locationDetailsValue : 'Hyderabad, India');
    }

    flatpickr("#inspectionDateAsPerNotification", {
                    mode: "multiple",
                    dateFormat: "d/m/Y"
                });


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

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LOGIN <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

  // if CVStatus is approved then the inspector becomes approvedInspector
   function approvedInspector(index, approved) {
        if(!approved) {
            document.getElementById("approvedInspectorName").value = "";
        }else{
            const proposedCVsTable = document.querySelector("#proposedCVsTable tbody");
            const row = proposedCVsTable.querySelector(`tr:nth-child(${index + 1})`);
            if (row) {
                const selectedOption = row.querySelector("select").selectedOptions[0];
                if (selectedOption) {
                    document.getElementById("approvedInspectorName").value = "";
                    document.getElementById("approvedInspectorName").value = selectedOption.textContent.trim()
                } else {
                    alert("No Inspector is selected.");
                }
            } else {
                console.log("Specified row not found in the proposedCVsTable.");
            }
        }
    }

   function initMap(inspectionLocation) {

       fetch('/api/inspectors?address=' + inspectionLocation )
       .then(response => response.json())
       .then(data => {
           for (const [locationKey, inspectors] of Object.entries(data)) {
               const userLocationArray = locationKey.split(',').map(Number); // Split and convert to numbers
               const userLocation = new google.maps.LatLng(userLocationArray[0], userLocationArray[1]); // Create LatLng object

              const map = new google.maps.Map(document.getElementById('map'), {
                  zoom: 10,
                  center: userLocation
              });

               // Add marker for user location
               const userMarker = new google.maps.Marker({
                   position: userLocation,
                   map: map,
                   title: "Inspection Location"
               });

               // Plot all inspectors for the current user location
               inspectors.forEach(inspector => {
                   const marker = new google.maps.Marker({
                       position: {
                           lat: inspector.location.lat,
                           lng: inspector.location.lng
                       },
                       map: map,
                       title: `${inspector.name}, Distance: ${inspector.distance}, Duration: ${inspector.duration}`,
                       icon: {
                           url: "http://maps.google.com/mapfiles/ms/icons/green-dot.png" // Green marker icon
                       }
                   });
               });
           }
               });

   }

   function showMapLoadingMessage() {
               const mapLoadingMessage = document.getElementById('mapLoadingMessage');
               mapLoadingMessage.style.display = 'block';

               // Assuming 'initMap' will handle the map loading process
               // Add a timeout or any custom logic to hide the message after map loading completes
               setTimeout(() => {
                   mapLoadingMessage.style.display = 'none';
               }, 5000); // Adjust the time as per map loading logic
           }

   function addCVRow() {

    const tableBody = document.querySelector("#proposedCVsTable tbody");
    const previousRow = tableBody.querySelector("tr:last-child");
    const index = tableBody.querySelectorAll("tr").length -1;

    if (previousRow) {
        const lastRadioInput = [...previousRow.querySelectorAll("input[type='radio']")].pop();
        if (lastRadioInput && lastRadioInput.value === "false" && !lastRadioInput.checked) {
            alert("Inspector is already approved.");
            return;
        }

        const newRow = previousRow.cloneNode(true); // Clone the previous row

        newRow.querySelectorAll("input, select").forEach(input => {

            input.id = input.id.replace(index, index + 1);
            input.name = input.name.replace(index, index + 1);

            if (input.tagName === "SELECT") {
                input.selectedIndex = 0; // Reset dropdowns
            } else if (input.type === "radio") {
                // Reset radio buttons
                if (input.value === "true") {
                    input.checked = false;
                }
                if (input.value === "false") {
                    input.checked = true;
                }
            } else if (input.type === "datetime-local") {
                input.value = ""; // Reset datetime-local input
            }
        });
        tableBody.appendChild(newRow); // Append the cloned row as a new row
    }

  }

   function deleteCVRow(button) {
      const tableBody = document.querySelector("#proposedCVsTable tbody");
      const rows = tableBody.querySelectorAll("tr");
      const row = button.closest("tr");
      if (row === rows[0]) {
          alert("At least one Inspector should be assigned to the Inspection !");
          return;
      }
      if (row) {
        const lastRadioInput = [...row.querySelectorAll("input[type='radio']")].pop();
        if (lastRadioInput && lastRadioInput.value === "false" && !lastRadioInput.checked) {
           document.getElementById("approvedInspectorName").value = "";
        }
      }
      row.remove();
  }
