document.addEventListener("DOMContentLoaded", function () {
    console.log("All events hooks here !!!");

    /** ================= OTP Section ================= **/

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

    /** ================= Map Initialization Section ================= **/

    const locationDetails = document.getElementById('inspectionLocationDetailsId');
    if (locationDetails) {
        const locationDetailsValue = locationDetails.value;
        initMap(locationDetailsValue ? locationDetailsValue : 'Hyderabad, India');
    }

    /** ================= Date Picker Initialization ================= **/

    if (document.getElementById('inspectionDateAsPerNotification')) {
        flatpickr("#inspectionDateAsPerNotification", {
            mode: "multiple",
            dateFormat: "d/m/Y",
            defaultDate: document.getElementById("inspectionDateAsPerNotification").value.split(",") // Prepopulate default dates
        });
    }

    var calendarEl = document.getElementById('calendar');
    if(calendarEl) {
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        events: function (fetchInfo, successCallback, failureCallback) {
            fetch('/calendar/inspection-stats?period=month')
                .then(response => response.json())
                .then(data => {

                    // Process the data before assigning it to events
                    let internationalEvents = [];
                    let indiaEvents = [];

                    // Define color codes for categorization
                    const colorCodes = {
                        international: {
                            INHOUSE_INSPECTOR: '#337dcc',  // Blue
                            TECHNICAL_COORDINATOR: '#337dcc',  // Blue
                            PARTNER_INSPECTOR: '#359423', // Green
                            FREELANCER: '#b1b215' // Yellow
                        },
                        india: {
                            INHOUSE_INSPECTOR: '#337dcc',  // Blue
                            TECHNICAL_COORDINATOR: '#337dcc',  // Blue
                            PARTNER_INSPECTOR: '#359423',  // Green
                            FREELANCER: '#b1b215' // Yellow
                        }
                    };

                    // Group the inspections by country and inspector type
                    data.forEach(item => {
                        const country = item.country;
                        const inspectorType = item.inspectorType;
                        const color = (colorCodes[country] && colorCodes[country][inspectorType]) ? colorCodes[country][inspectorType] : '#CCCCCC'; // Default to gray if no match

                        // Format the event
                        const event = {
                            id: item.id,
                            title: item.title,
                            start: item.start,
                            backgroundColor: color,
                            borderColor: color
                        };

                        // Add the event to the respective section
                        if (country === 'international') {
                            internationalEvents.push(event);
                        } else if (country === 'india') {
                            indiaEvents.push(event);
                        }
                    });

                    calendar.internationalEvents = internationalEvents;
                    calendar.indiaEvents = indiaEvents;

                    successCallback([...internationalEvents, ...indiaEvents]);
                })
                .catch(error => {
                    console.error('Error fetching events:', error);
                    failureCallback(error); // Pass the error to the calendar
                });
        },
        customButtons: {
            internationalView: {
                text: 'International',
                click: function () {
                    calendar.removeAllEvents();
                    calendar.addEventSource(calendar.internationalEvents);
                }
            },
            indiaView: {
                text: 'India',
                click: function () {
                    calendar.removeAllEvents();
                    calendar.addEventSource(calendar.indiaEvents);
                }
            }
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'internationalView,indiaView'
        }
    });
    calendar.render();
    }

});

/** ================= Universal Form Validation ============== **/

document.addEventListener('DOMContentLoaded', function () {
    'use strict'

    // Fetch all forms we want to apply validation to
    var forms = document.querySelectorAll('.needs-validation');

    // Loop over them and prevent submission
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');
        }, false);
    });
});

/** ================= Universal Data Table ================ **/

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

/** ================= Inspector ================= **/

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

function openInspectorForm() {
 window.location.href = '/inspectors/form';
}

function redirectToEditForm(inspectorId) {
 window.location.href = '/inspectors/edit/' + inspectorId;
}

function redirectToViewForm(inspectorId) {
 window.location.href = '/inspectors/view/' + inspectorId;
}

/** ================= Inspection ================= **/

function redirectToNewInspection() {
 window.location.href = '/inspection/new';
}

function redirectToEditInspection(inspectionId) {
 window.location.href = '/inspection/edit/' + inspectionId;
}

function redirectToViewInspection(inspectionId) {
 window.location.href = '/inspection/view/' + inspectionId;
}

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

function addCVRow() {

    const tableBody = document.querySelector("#proposedCVsTable tbody");
    const previousRow = tableBody.querySelector("tr:last-child");
    const index = tableBody.querySelectorAll("tr").length -1;

    if (previousRow) {
        const lastSelectInput = [...previousRow.querySelectorAll("select")].pop();
        if (lastSelectInput && lastSelectInput.value === "true") {
            alert("Inspector is already approved.");
            return;
        }

        const newRow = previousRow.cloneNode(true); // Clone the previous row

        newRow.querySelectorAll("input, select, button").forEach(input => {

            input.id = input.id.replace(index, index + 1);
            input.name = input.name.replace(index, index + 1);

            if (input.tagName === "SELECT") {
                input.selectedIndex = 0; // Reset dropdowns
            } else if (input.type === "datetime-local"  || (input.type === "hidden" && input.id.includes("cvCertificatesLink"))) {
                input.value = ""; // Reset datetime-local input
            } else if (input.type === "button" && input.hasAttribute("data-index")) {
               input.setAttribute("data-index", index + 1); // Update button data-index
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
        const lastSelectInput = [...row.querySelectorAll("select")].pop();
        if (lastSelectInput && lastSelectInput.value === "true") {
           document.getElementById("approvedInspectorName").value = "";
        }
      }
      row.remove();
  }

function initMap(inspectionLocation) {

       fetch('/api/inspectors?address=' + inspectionLocation )
       .then(response => response.json())
       .then(data => {
           for (const [locationKey, inspectorsMap] of Object.entries(data)) {
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


                // Iterate over the map of inspector types and their distances
                for (const [inspectorType, inspectors] of Object.entries(inspectorsMap)) {
                    const iconUrl = (type) => {
                        switch (type) {
                            case 'PARTNER_INSPECTOR':
                                return "http://maps.google.com/mapfiles/ms/icons/green-dot.png"; // Green icon for Partner
                            case 'FREELANCER':
                                return "http://maps.google.com/mapfiles/ms/icons/yellow-dot.png"; // Yellow icon for Freelancer
                            case 'INHOUSE_INSPECTOR':
                                return "http://maps.google.com/mapfiles/ms/icons/blue-dot.png"; // Blue icon for Inhouse
                            case 'TECHNICAL_COORDINATOR':
                                return "http://maps.google.com/mapfiles/ms/icons/purple-dot.png"; // Purple icon for Technical Coordinator
                            default:
                                return "http://maps.google.com/mapfiles/ms/icons/green-dot.png"; // Default icon (red)
                        }
                    };

                    inspectors.forEach(inspector => {
                        const inspectorMarker = new google.maps.Marker({
                            position: {
                                lat: inspector.location.lat,
                                lng: inspector.location.lng
                            },
                            map: map,
                            title: `${inspector.name}, Distance: ${inspector.distance}, Duration: ${inspector.duration}`,
                            icon: {
                                url: iconUrl(inspectorType) // Set marker color based on inspectorType
                            }
                        });
                    });
                }


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

function openCertificateLinkPopup(button) {
    const index = button.getAttribute('data-index');
    // Get the current value of the hidden input field for the certificate link
    const certificateLinkField = document.getElementById('cvCertificatesLink' + index);
    const currentLink = certificateLinkField ? certificateLinkField.value : '';

    // Prompt the user to enter or edit the link
    const newLink = prompt("Enter the certificate link:", currentLink);

    // If a new link is provided, update the hidden input field value
    if (newLink !== null) {
    certificateLinkField.value = newLink;
    }
}

function openReferenceDocumentsLinkPopup() {

    // Get the current value of the hidden input field for the certificate link
    const referenceDocumentsLinkField = document.getElementById('referenceDocumentsLink');
    const currentLink = referenceDocumentsLinkField ? referenceDocumentsLinkField.value : '';

    // Prompt the user to enter or edit the link
    const newLink = prompt("Enter the reference documents link:", currentLink);

    // If a new link is provided, update the hidden input field value
    if (newLink !== null) {
    referenceDocumentsLinkField.value = newLink;
    }
}

/** ================== Technical Coordinator =========== **/

function redirectToTechnicalCoordinatorEditInspection(inspectionId) {
 window.location.href = 'inspection/edit/' + inspectionId;
 window.history.pushState(null, "", window.location.href);
 window.onpopstate = function () {
     window.history.pushState(null, "", window.location.href);
 };
}

/** =================== Contract Review ================= **/

function prepareContractReview(inspectionId) {
  const url = `/contractReview/edit/` + inspectionId;
  const width = screen.width * 0.9;
  const height = screen.height * 0.9;
  const left = (screen.width - width) / 2;
  const top = (screen.height - height) / 2;
  window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
 }

 function viewContractReview(inspectionId) {
   const url = `/contractReview/view/` + inspectionId;
   const width = screen.width * 0.9;
   const height = screen.height * 0.9;
   const left = (screen.width - width) / 2;
   const top = (screen.height - height) / 2;
   window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
  }

/** =================== Inspection Advise ================= **/

function prepareInspectionAdvise(inspectionId) {
  const url = `/inspectionAdvise/edit/` + inspectionId;
  const width = screen.width * 0.9;
  const height = screen.height * 0.9;
  const left = (screen.width - width) / 2;
  const top = (screen.height - height) / 2;
  window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
 }

 function viewInspectionAdvise(inspectionId) {
   const url = `/inspectionAdvise/view/` + inspectionId;
   const width = screen.width * 0.9;
   const height = screen.height * 0.9;
   const left = (screen.width - width) / 2;
   const top = (screen.height - height) / 2;
   window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
  }
/** ====================== PQR ========================== **/

function editPQRForm(inspectorId, inspectionId) {
 const url = `/pqr/edit/` + inspectorId + '/' + inspectionId;
 const width = screen.width * 0.9;
 const height = screen.height * 0.9;
 const left = (screen.width - width) / 2;
 const top = (screen.height - height) / 2;
 window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
}

function viewPQRForm(inspectorId) {
 const url = `/pqr/view/` + inspectorId;
 const width = screen.width * 0.9;
 const height = screen.height * 0.9;
 const left = (screen.width - width) / 2;
 const top = (screen.height - height) / 2;
 window.open(url, '_blank', `width=${width},height=${height},left=${left},top=${top}`);
}

document.addEventListener('DOMContentLoaded', function () {
    const successMessage = document.querySelector('#successMessage');
    if (successMessage && successMessage.textContent.trim() !== '') {
        const form = document.querySelector('div.page-container');
        if (form) {
            form.style.filter = 'blur(5px)';
        }

        const successMessageElement = document.createElement('div');
        successMessageElement.className = 'alert alert-success';
        successMessageElement.textContent = successMessage.textContent.trim();
        const mainElement = document.querySelector('main');
        if (mainElement) {
            mainElement.prepend(successMessageElement);
        }

        setTimeout(() => {
            window.close();
        }, 3000);
    }
});

// Function to calculate and update the total score
function calculateScore() {
    let totalScore = 0;
    const fields = ["education", "experience", "englishSkills", "professionalQualifications"];

    // Iterate through the predefined fields
    fields.forEach((fieldName) => {
      // Get the selected radio buttons or checkboxes for the field
      const selectedOptions = document.querySelectorAll(`input[name="${fieldName}"]:checked`);

      selectedOptions.forEach((selectedOption) => {
        // Extract data-rating and data-factor values
        const rating = parseFloat(selectedOption.getAttribute("data-rating") || 0);
        const factor = parseFloat(selectedOption.getAttribute("data-factor") || 0);

        // Calculate the score for this option
        const optionScore = rating * factor;

        // Add the option score to the cumulative total score
        totalScore += optionScore;
      });
    });

    // Update the corresponding score field on the form
    const scoreField = document.getElementById("score");
    if (scoreField) {
      scoreField.value = totalScore; // Assign the calculated value dynamically
    }
}


/** ================== Inspector =========== **/

function redirectToInspectorViewInspection(inspectionId) {
 window.location.href = 'inspection/view/' + inspectionId;
}

/** ================== Login ================= **/

function disableButton() {
}

/** ================== Business Dashboard ================= **/




