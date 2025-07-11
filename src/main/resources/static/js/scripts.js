let lastCoordinatorData = null;
let lastTechnicalData = null;
let lastInspectorData = null;

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

   if (["Coordinator Dashboard", "Admin Dashboard", "Inspector Dashboard", "Technical Coordinator Dashboard"].includes(document.title)) {
       const stats = ["Coordinator Dashboard", "Inspector Dashboard", "Technical Coordinator Dashboard"].includes(document.title);
       const reports = ["Coordinator Dashboard", "Admin Dashboard"].includes(document.title);
       const context = {
           "Coordinator Dashboard": "coordinator",
           "Technical Coordinator Dashboard": "technical-coordinator",
           "Inspector Dashboard": "inspector"
       }[document.title] || null;

       // Initialize date inputs
       initializeDateInputs(context, stats, reports);
   }

   if (["Business Dashboard"].includes(document.title)) {
       initializeBD();
   }
   if(["Inspectors Management"].includes(document.title)) {
    document.getElementById('exportInspectorsExcelBtn').addEventListener('click', () => exportInspectorsData('reports', 'excel'));
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

    /** ================== Calendar View ======================== **/

    var calendarEl = document.getElementById('calendar');
    if(calendarEl) {
    var calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        events: function (fetchInfo, successCallback, failureCallback) {
                    if (!calendar || !calendar.hasLoadedEvents) { // Ensure events load only once
                        fetch('/calendar/inspection-stats?period=quarter')
                            .then(response => response.json())
                            .then(data => {
                                let internationalEvents = [];
                                let indiaEvents = [];

                                const colorCodes = {
                                    yes: {
                                        INHOUSE_INSPECTOR: '#337dcc',
                                        TECHNICAL_COORDINATOR: '#337dcc',
                                        PARTNER_INSPECTOR: '#337dcc',
                                        FREELANCER: '#337dcc'
                                    },
                                    no: {
                                        INHOUSE_INSPECTOR: '#8ec6a4',
                                        TECHNICAL_COORDINATOR: '#8ec6a4',
                                        PARTNER_INSPECTOR: '#8ec6a4',
                                        FREELANCER: '#8ec6a4'
                                    },
                                    reportReview: {
                                       TECHNICAL_COORDINATOR: '#b1b215'
                                    }
                                };

                                data.forEach(item => {
                                    const country = item.country;
                                    const onField = item.onField;
                                    const inspectorType = item.inspectorType;
                                    const color = (colorCodes[onField] && colorCodes[onField][inspectorType]) ? colorCodes[onField][inspectorType] : '#CCCCCC';

                                    const event = {
                                        id: item.id,
                                        title: item.title,
                                        start: item.start,
                                        backgroundColor: color,
                                        borderColor: color
                                    };

                                    if (country === 'international') {
                                        internationalEvents.push(event);
                                    } else if (country === 'india') {
                                        indiaEvents.push(event);
                                    }
                                });

                                calendar.internationalEvents = internationalEvents;
                                calendar.indiaEvents = indiaEvents;
                                calendar.hasLoadedEvents = true; // Set a flag to indicate events are loaded

                                successCallback([...indiaEvents]); // Show only India events by default
                            })
                            .catch(error => {
                                console.error('Error fetching events:', error);
                                failureCallback(error);
                            });
                    } else {
                        successCallback([]); // Do not fetch new events on navigation
                    }
        },
        customButtons: {
            internationalView: {
                text: 'International',
                click: function () {
                    calendar.removeAllEvents();
                    calendar.addEventSource(calendar.internationalEvents);

                    document.querySelector('.fc-internationalView-button').classList.add('highlighted-button');
                    document.querySelector('.fc-indiaView-button').classList.remove('highlighted-button');
                }
            },
            indiaView: {
                text: 'India',
                click: function () {
                    calendar.removeAllEvents();
                    calendar.addEventSource(calendar.indiaEvents);

                    document.querySelector('.fc-indiaView-button').classList.add('highlighted-button');
                    document.querySelector('.fc-internationalView-button').classList.remove('highlighted-button');
                }
            },
            today: {
                text: 'Today',
                click: function () {
                    calendar.today(); // Navigate to today's date without adding any data
                }
            }
        },
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'indiaView internationalView'
        },
        eventClassNames: function (arg) {
            return ['custom-event-style'];
        },
        dayCellDidMount: function (arg) {
            if (arg.date.getDay() === 0 || arg.date.getDay() === 6) {
                arg.el.style.backgroundColor = '#f0f8ff';
            }
            // Ensure every event displays its title without wrapping and includes tooltips
            if (arg.el.querySelector('.fc-daygrid-event')) {
                arg.el.style.whiteSpace = 'nowrap';
                arg.el.style.overflow = 'hidden';
                arg.el.title = info.event.title;
            }
        },
        dayCellContent: function (arg) {
            return {
                html: '<span class="circle"></span><div>' + arg.dayNumberText + '</div>'
            };
        },
        dayMaxEventRows: true,
        height: 'auto',
        firstDay: 1,
        navLinks: true
    });

    calendarEl.style.backgroundColor = '#f8f9fa';
    calendarEl.style.border = '1px solid #ddd';
    calendarEl.style.padding = '10px';
    calendarEl.style.borderRadius = '5px';

    calendar.render();

    // Highlight default 'India' button and add spacing between buttons
    const internationalViewButton = document.querySelector('.fc-internationalView-button');
    const indiaViewButton = document.querySelector('.fc-indiaView-button');
    indiaViewButton.classList.add('highlighted-button'); // Highlight 'India' button by default

    // Add spacing and styling for the buttons
    internationalViewButton.style.marginRight = '5px';
    indiaViewButton.style.marginRight = '5px';

    // Additional CSS for highlighted button
    const style = document.createElement('style');
    style.innerHTML = `
      .highlighted-button {
          background-color: var(--fc-button-bg-color, #2C3E50);
          color: white;
          border: none;
      }
      .fc-button:not(.highlighted-button) {
          background-color: white;
          color: #007bff;
          border: 1px solid #ddd;
      }
      .fc-button {
          margin-right: 5px;
          padding: 5px 15px;
          border-radius: 5px;
          font-weight: bold;
      }
      .day-cell .section {
          margin-top: 5px;
          padding: 5px;
          border-radius: 3px;
      }
      .fc-daygrid-event {
           position: relative;
           white-space: wrap;
           border-radius: 3px;
           font-size: .60rem;
           font-size: var(--fc-small-font-size, .85em);
      }
    `;
    document.head.appendChild(style);
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
 const today = new Date().toISOString().split('T')[0];
 newRow.innerHTML = `
     <td><input type="text" name="certificates[${rowCount}].name" class="form-control"
                            placeholder="Certificate Name" required
                            minlength="2" maxlength="500"
                            oninvalid="this.setCustomValidity('Please enter a certificate name (2-500 characters)')"
                            oninput="this.setCustomValidity('')"/>
                     <div class="invalid-feedback">
                         Please provide a certificate name (2-500 characters).
                     </div></td>
     <td><input type="date" name="certificates[${rowCount}].dateIssued" class="form-control issue-date"
                            required max="${today}"
                            onchange="validateCertificateDates(this)"
                            oninvalid="this.setCustomValidity('Please select a valid issue date (not in future)')"
                            oninput="this.setCustomValidity('')"/>
                     <div class="invalid-feedback">
                         Please provide a valid issue date (not in future).
                     </div></td>
     <td><input type="date" name="certificates[${rowCount}].expiryDate" class="form-control expiry-date"
                            required
                            onchange="validateCertificateDates(this)"
                            oninvalid="this.setCustomValidity('Please select a valid expiry date (after issue date)')"
                            oninput="this.setCustomValidity('')"/>
                     <div class="invalid-feedback">
                         Please provide a valid expiry date (after issue date).
                     </div></td>
     <td><input type="text" name="certificates[${rowCount}].issuer" class="form-control"
                            placeholder="Issuer" required
                            minlength="2" maxlength="500"
                            oninvalid="this.setCustomValidity('Please enter an issuer name (2-500 characters)')"
                            oninput="this.setCustomValidity('')"/>
                     <div class="invalid-feedback">
                         Please provide an issuer name (2-500 characters).
                     </div></td>
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

function approvedInspector(element) {
    

    const index = Array.from(element.closest('tr').parentElement.children).indexOf(element.closest('tr'));
    const approved = element.value;

    // Get the select box that stores the approved inspector names
    const approvedInspectorSelectBox = document.getElementById("approvedInspectorNames");

    // Get the table row containing the inspector data
    const proposedCVsTable = document.querySelector("#proposedCVsTable tbody");
    const row = proposedCVsTable.querySelector(`tr:nth-child(${index + 1})`);

    if (row) {
        // Get the currently selected inspector option from the dropdown
        const selectedOption = row.querySelector("select").selectedOptions[0];
        if (selectedOption) {
            const inspectorName = selectedOption.textContent.trim();
            const inspectorId = selectedOption.value.trim();

            if (approved == "true") {
                // Check if the inspector is already in the select box
                const exists = Array.from(approvedInspectorSelectBox.options).some(
                    (option) => option.value === inspectorName
                );

                if (!exists) {
                    // Add a new option to the select box for the approved inspector
                    const newOption = document.createElement("option");
                    newOption.value = inspectorName;
                    newOption.textContent = inspectorName;
                    newOption.selected = true; // Set the new option as selected
                    approvedInspectorSelectBox.appendChild(newOption);
                }
            } else {
                // Remove the inspector from the select box if they are disapproved
                Array.from(approvedInspectorSelectBox.options).forEach((option) => {
                    if (option.value === inspectorName) {
                        approvedInspectorSelectBox.removeChild(option);
                    }
                });
            }

            console.log(
                "Current options in approvedInspectorNames:",
                Array.from(approvedInspectorSelectBox.options).map((option) => option.value)
            );
        } else {
            alert("No Inspector is selected.");
        }
    } else {
        console.log("Specified row not found in the proposedCVsTable.");
    }
}



function addCVRow() {

    const tableBody = document.querySelector("#proposedCVsTable tbody");
    const previousRow = tableBody.querySelector("tr:last-child");
    const index = tableBody.querySelectorAll("tr").length -1;

    if (previousRow) {
        const lastSelectInput = [...previousRow.querySelectorAll("select")].pop();
        if (lastSelectInput && lastSelectInput.value === "true") {
            const userConfirmed = confirm("Inspector is already approved. Do you want to proceed?");
                if (!userConfirmed) {
                     return;
                }
        }

        const newRow = previousRow.cloneNode(true); // Clone the previous row

        newRow.querySelectorAll("input, select, button").forEach(input => {

            input.id = input.id.replace(index, index + 1);
            input.name = input.name.replace(index, index + 1);

            if (input.tagName === "SELECT") {
                input.selectedIndex = 0; // Reset dropdowns
            } else if (input.type === "datetime-local"  || input.type === "hidden") {
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
      if (row === rows[0] && rows.length === 1) {
          alert("At least one Inspector should be assigned to the Inspection !");
          return;
      }
      if (row) {
        const lastSelectInput = [...row.querySelectorAll("select")].pop();
        if (lastSelectInput && lastSelectInput.value === "true") {
           const inspectorName = row.querySelector("select").selectedOptions[0].textContent.trim();
            // Remove the inspector from the select box if they are disapproved
           Array.from(document.getElementById("approvedInspectorNames").options).forEach((option) => {
               if (option.value === inspectorName) {
                   document.getElementById("approvedInspectorNames").removeChild(option);
               }
           });
        }
      }
      row.remove();
  }

function addReportRow() {
    const masterDiv = document.getElementById("inspectionReportsId");
    if (!masterDiv) {
        console.error("'inspectionReportsId' not found.");
        return;
    }

    const targetDiv = document.getElementById("inspectionReportsContainerHub");
    if (!targetDiv) {
        console.error("'inspectionReportsContainerHub' not found.");
        return;
    }

    const lastContainer = masterDiv.querySelector(".inspection-report:last-child");
    if (!lastContainer) {
        console.error("No '.inspection-report' container found to clone.");
        return;
    }

    // Clone the last container and determine the new index
    const newContainer = lastContainer.cloneNode(true);
    const newIndex = masterDiv.querySelectorAll(".inspection-report").length;

    updateContainerAttributes(newContainer, newIndex);

    // Append the new container to the target div
    targetDiv.appendChild(newContainer);
}

function updateContainerAttributes(container, newIndex) {
    // Helper function to update an attribute by replacing the index
    const updateAttribute = (element, attribute, newIndex) => {
        const attrValue = element.getAttribute(attribute);
        if (attrValue) {
            element.setAttribute(attribute, attrValue.replace(/\[(\d+)\]/g, `[${newIndex}]`));
        }
    };

    // Update attributes in the cloned container
    container.querySelectorAll("[id], [name], label[for], [data-index]").forEach((element) => {
        updateAttribute(element, "id", newIndex);
        updateAttribute(element, "name", newIndex);
        updateAttribute(element, "for", newIndex);

        // Special case for Thymeleaf 'th:field' attribute
        const nameAttr = element.getAttribute("name");
        if (nameAttr) {
            element.setAttribute("th:field", `*{${nameAttr}}`);
        }

        // Update data-index attributes for buttons
        if (element.tagName === "BUTTON" && element.hasAttribute("data-index")) {
            element.setAttribute("data-index", newIndex);
        }

        // Reset input, textarea, and select element values
        resetElementValue(element);
    });
}

function resetElementValue(element) {
    if (element.tagName === "INPUT" || element.tagName === "TEXTAREA") {
        element.value = ""; // Clear input or textarea values
    } else if (element.tagName === "SELECT") {
        element.selectedIndex = 0; // Reset dropdown to default state
    }
}


function deleteReportRow(button) {
    // Select the main container holding all the IR/FR documents
    const containerHub = document.getElementById("inspectionReportsContainerHub");
    const containers = containerHub.querySelectorAll(".inspection-report"); // All containers (or rows)

    // Find the container (div) that corresponds to the clicked button
    const container = button.closest(".inspection-report");

    // Prevent deletion of the first container
    if (container === containers[0]) {
        alert("At least one IR/FR document should be present!");
        return;
    }

    // Handle resetting values if applicable
    if (container) {
        const lastSelectInput = [...container.querySelectorAll("select")].pop();
        if (lastSelectInput && lastSelectInput.value === "true") {
            const reportNumberField = document.getElementById("inspectionReportNumber");
            if (reportNumberField) {
                reportNumberField.value = ""; // Clear the value of the specific field
            }
        }
        container.remove(); // Remove the selected container
    }
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

function openReportsLinkPopup(button) {
    const index = button.getAttribute('data-index');
    // Get the current value of the hidden input field for the certificate link
    const reportLinkField = document.getElementById('reportLink[' + index + ']');
    const currentLink = reportLinkField ? reportLinkField.value : '';

    // Prompt the user to enter or edit the link
    const newLink = prompt("Enter the reports link:", currentLink);

    // If a new link is provided, update the hidden input field value
    if (newLink !== null) {
    reportLinkField.value = newLink;
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

function updateTechCoordComment(reportItem) {
    // Extract remarks and id from the current reportItem
    const remarks = reportItem.querySelector('textarea').value.trim();
    const id = reportItem.querySelector('input[type="hidden"]').value.trim();

    // Find the server message span and button from the current reportItem
    const serverMessageContainer = reportItem.querySelector("#serverMessageSpan");
    const commentButton = reportItem.querySelector("button.btn-info"); // Target the comment button

    if (commentButton) {
        // Disable the button and update the text to "Commenting..."
        const originalText = commentButton.textContent;
        commentButton.textContent = "Commenting...";
        commentButton.disabled = true;

        // Build the JSON payload
        const payload = {
            id: id,
            remarks: remarks,
        };

        // Send the POST request with a JSON body
        fetch('/technical-coordinator/inspectionReports/updateComments', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json', // Indicates the request body is JSON
            },
            body: JSON.stringify(payload), // Converts the payload to a JSON string
        })
        .then((response) => {
            // Ensure we process the body only once
            if (!response.ok) {
                return response.text().then((errorMessage) => {
                    throw new Error(errorMessage || 'Failed to update remarks');
                });
            }
            return response.text(); // Read response body only once
        })
        .then((result) => {
            // Show success or failure message based on the result
            if (serverMessageContainer) {
                if (result === "Remarks updated successfully!") {
                    serverMessageContainer.textContent = "Comment updated successfully!";
                    serverMessageContainer.classList.remove("text-danger");
                    serverMessageContainer.classList.add("text-success");
                } else {
                    serverMessageContainer.textContent = result || "Failed to update comment. Please try again.";
                    serverMessageContainer.classList.remove("text-success");
                    serverMessageContainer.classList.add("text-danger");
                }

                // Remove the message after 8 seconds
                setTimeout(() => {
                    serverMessageContainer.textContent = "";
                }, 8000);
            }
        })
        .catch((error) => {
            // Handle any unexpected errors
            console.error('Error:', error);

            if (serverMessageContainer) {
                serverMessageContainer.textContent = "An unexpected error occurred. Please try again.";
                serverMessageContainer.classList.remove("text-success");
                serverMessageContainer.classList.add("text-danger");

                // Remove the message after 8 seconds
                setTimeout(() => {
                    serverMessageContainer.textContent = "";
                }, 8000);
            }
        })
        .finally(() => {
            // Re-enable the button and revert its text
            if (commentButton) {
                commentButton.textContent = originalText;
                commentButton.disabled = false;
            }
        });
    }
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

  // Add event listener to the entire table
  document.addEventListener('DOMContentLoaded', () => {
      const table = document.getElementById("contractDocumentTableId");
      if(!table){
        return;
      }
      const submissionListTextarea = document.getElementById("submissionList");

      // Attach an event listener to the radio buttons inside the table
      table.addEventListener("change", (event) => {
          if (event.target.type === "radio") {
              // Get the parent row of the selected radio button
              const row = event.target.closest("tr");
              const descriptionInput = row.querySelector("span"); // Document description field
              const documentDescription = descriptionInput.innerText; // Get the document description
              const selectedValue = event.target.value; // Get the value of the selected radio button ("Yes" or "No")

              // Handle the selection logic
              if (selectedValue.toLowerCase() === "yes") {
                  // Add the description to submissionList if selected as Yes
                  addToSubmissionList(documentDescription, submissionListTextarea);
              } else {
                  // Remove the description from submissionList if selected as No
                  removeFromSubmissionList(documentDescription, submissionListTextarea);
              }
          }
      });

      // Function to add a description to the submissionList textarea
      function addToSubmissionList(description, textarea) {
          const items = textarea.value.split("\n").filter(item => item.trim() !== ""); // Get current lines (non-empty)
          if (!items.includes(description)) {
              items.push(description); // Add description if not already in the list
          }
          textarea.value = items.join("\n"); // Update the textarea
      }

      // Function to remove a description from the submissionList textarea
      function removeFromSubmissionList(description, textarea) {
          const items = textarea.value.split("\n").filter(item => item.trim() !== ""); // Get current lines (non-empty)
          const updatedItems = items.filter(item => item !== description); // Remove the description
          textarea.value = updatedItems.join("\n"); // Update the textarea
      }
  });

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

 /*<![CDATA[*/

   function initializeBD() {
        // Initialize toggle for stat cards
        initializeStatCardToggles();

        // Initialize tab navigation
        initializeTabNavigation();

        // Initialize comparison chart
        initializeComparisonChart();

        // Set current date for date inputs
        initializeDateInputs();
        updateComparisonChart();

        initializeSelects();
    }

    function initializeSelects() {
        // Set up listeners for individual select changes
        document.getElementById('coordinatorSelect').addEventListener('change', function() {
            handleCoordinatorChange(this);

        });
        document.getElementById('technicalCoordinatorSelect').addEventListener('change', function() {
            handleTechnicalCoordinatorChange(this);

        });
        document.getElementById('inspectorSelect').addEventListener('change', function() {
            handleInspectorChange(this);
        });

        // Also update comparison chart when period changes
        document.getElementById('coordinatorPeriodSelect').addEventListener('change', updateComparisonChart);
        document.getElementById('technicalPeriodSelect').addEventListener('change', updateComparisonChart);
        document.getElementById('inspectorPeriodSelect').addEventListener('change', updateComparisonChart);
    }
    function initializeStatCardToggles() {
        const statHeaders = document.querySelectorAll('.bd-stat-header');

        statHeaders.forEach(header => {
            header.addEventListener('click', function() {
                const targetId = this.getAttribute('data-target');
                const content = document.getElementById(targetId);
                const icon = this.querySelector('.bd-dropdown-icon');

                content.classList.toggle('expanded');
                icon.classList.toggle('active');
            });
        });
    }

   function initializeTabNavigation() {
    const tabButtons = document.querySelectorAll('.bd-tab-button');

    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons and panels
            document.querySelectorAll('.bd-tab-button').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.bd-chart-panel').forEach(panel => panel.classList.remove('active'));

            // Add active class to clicked button
            this.classList.add('active');

            // Show the corresponding panel
            const targetPanelId = this.getAttribute('data-target');
            document.getElementById(targetPanelId).classList.add('active');

        });
    });
}
    function initializeDateInputs() {
        // Set default dates to 30 days ago and today
        const today = new Date();
        const thirtyDaysAgo = new Date();
        thirtyDaysAgo.setDate(today.getDate() - 30);

        // Format dates for input fields
        const formatDate = (date) => {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');
            return `${year}-${month}-${day}`;
        };

        // Set date values for all custom range inputs
        const startDateInputs = document.querySelectorAll('[id$="StartDate"]');
        const endDateInputs = document.querySelectorAll('[id$="EndDate"]');

        startDateInputs.forEach(input => input.value = formatDate(thirtyDaysAgo));
        endDateInputs.forEach(input => input.value = formatDate(today));
    }

    function initializeComparisonChart() {
        const ctx = document.getElementById('comparisonChart').getContext('2d');
        return new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                datasets: [{
                    label: 'Select users above to compare performance',
                    data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                    borderColor: '#858796',
                    backgroundColor: 'rgba(133, 135, 150, 0.1)',
                    borderWidth: 1,
                    borderDash: [5, 5],
                    pointRadius: 0,
                    fill: true
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: 'Inspections Completed'
                        },
                        grid: {
                            display: true,
                            drawBorder: false,
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            boxWidth: 12,
                            padding: 20,
                            font: {
                                size: 12
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(255, 255, 255, 0.9)',
                        titleColor: '#5a5c69',
                        bodyColor: '#5a5c69',
                        borderColor: '#e3e6f0',
                        borderWidth: 1,
                        cornerRadius: 4,
                        displayColors: true,
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) {
                                    label += ': ';
                                }
                                if (context.parsed.y !== null) {
                                    label += context.parsed.y + ' inspections';
                                }
                                return label;
                            }
                        }
                    }
                }
            }
        });
    }

    // Period change handlers
    function handleCoordinatorPeriodChange() {
        const periodSelect = document.getElementById('coordinatorPeriodSelect');
        const customRangeContainer = document.getElementById('coordinatorCustomRangeContainer');

        if (periodSelect.value === 'custom') {
            customRangeContainer.style.display = 'block';
        } else {
            customRangeContainer.style.display = 'none';
            updateCoordinatorChartWithPeriod(periodSelect.value);
        }
    }

    function handleTechnicalPeriodChange() {
        const periodSelect = document.getElementById('technicalPeriodSelect');
        const customRangeContainer = document.getElementById('technicalCustomRangeContainer');

        if (periodSelect.value === 'custom') {
            customRangeContainer.style.display = 'block';
        } else {
            customRangeContainer.style.display = 'none';
            updateTechnicalChartWithPeriod(periodSelect.value);
        }
    }

    function handleInspectorPeriodChange() {
        const periodSelect = document.getElementById('inspectorPeriodSelect');
        const customRangeContainer = document.getElementById('inspectorCustomRangeContainer');

        if (periodSelect.value === 'custom') {
            customRangeContainer.style.display = 'block';
        } else {
            customRangeContainer.style.display = 'none';
            updateInspectorChartWithPeriod(periodSelect.value);
        }
    }

 function applyCoordinatorCustomRange() {
    const period = 'custom'; // Force custom period
    const selectedCoordinator = document.getElementById('coordinatorSelect').value;

    if (selectedCoordinator) {
        fetchCoordinatorMetricData(selectedCoordinator, period);
    } else {
        showNotification('Please select a coordinator first', 'warning');
    }
}

 function applyTechnicalCustomRange() {
    const period = 'custom'; // Force custom period
    const selectedTechCoordinator = document.getElementById('technicalCoordinatorSelect').value;

    if (selectedTechCoordinator) {
        fetchTechnicalMetricData(selectedTechCoordinator, period);
    } else {
        showNotification('Please select a technical coordinator first', 'warning');
    }
}

function applyInspectorCustomRange() {
    const period = 'custom'; // Force custom period
    const selectedInspector = document.getElementById('inspectorSelect').value;

    if (selectedInspector) {
        fetchInspectorMetricData(selectedInspector, period);
    } else {
        showNotification('Please select an inspector first', 'warning');
    }
}

    // Update chart with period
    function updateCoordinatorChartWithPeriod(period) {
        const selectedCoordinator = document.getElementById('coordinatorSelect').value;

        if (selectedCoordinator) {
            fetchCoordinatorMetricData(selectedCoordinator, period);
        }
    }

    function updateTechnicalChartWithPeriod(period) {
        const selectedTechCoordinator = document.getElementById('technicalCoordinatorSelect').value;

        if (selectedTechCoordinator) {
            fetchTechnicalMetricData(selectedTechCoordinator, period);
        }
    }

    function updateInspectorChartWithPeriod(period) {
        const selectedInspector = document.getElementById('inspectorSelect').value;

        if (selectedInspector) {
            fetchInspectorMetricData(selectedInspector, period);
        }
    }

    // Update chart with date range
    function updateCoordinatorChartWithDateRange(startDate, endDate) {
        const selectedCoordinator = document.getElementById('coordinatorSelect').value;

        if (selectedCoordinator) {
            fetchCoordinatorDateRangeData(selectedCoordinator, startDate, endDate);
        } else {
            showNotification('Please select a coordinator first', 'warning');
        }
    }

    // Handler for individual selection
    function handleCoordinatorChange(selectElement) {
        const selectedCoordinatorEmail = selectElement.value;
        const period = document.getElementById('coordinatorPeriodSelect').value;

        if (!selectedCoordinatorEmail) return;

        // Remove empty state and show loading
        removeEmptyState('coordinatorPerformanceContainer');
        showBDLoadingIndicator('coordinatorPerformanceContainer');

        fetchCoordinatorMetricData(selectedCoordinatorEmail, period);
    }

    function handleTechnicalCoordinatorChange(selectElement) {
        const selectedTechnicalCoordinator = selectElement.value;
        const period = document.getElementById('technicalPeriodSelect').value;

        if (!selectedTechnicalCoordinator) return;

        // Remove empty state and show loading
        removeEmptyState('technicalCoordinatorContainer');
        showBDLoadingIndicator('technicalCoordinatorContainer');

        fetchTechnicalMetricData(selectedTechnicalCoordinator, period);

    }

    function handleInspectorChange(selectElement) {
        const selectedInspector = selectElement.value;
        const period = document.getElementById('inspectorPeriodSelect').value;

        if (!selectedInspector) return;

        // Remove empty state and show loading
        removeEmptyState('inspectorPerformanceContainer');
        showBDLoadingIndicator('inspectorPerformanceContainer');

        fetchInspectorMetricData(selectedInspector, period);
    }

function fetchMetricData(role, identifier, period) {
    const roleConfig = {
        coordinator: {
            containerId: 'coordinatorPerformanceContainer',
            urlPath: 'coordinator-stats',
            startDateId: 'coordinatorStartDate',
            endDateId: 'coordinatorEndDate',
            totalDisplayId: 'coordinatorTotalDisplay',
            lastDataKey: 'lastCoordinatorData'
        },
        technical: {
            containerId: 'technicalCoordinatorContainer',
            urlPath: 'technical-coordinator-stats',
            startDateId: 'technicalStartDate',
            endDateId: 'technicalEndDate',
            totalDisplayId: 'techCoordinatorTotalDisplay',
            lastDataKey: 'lastTechnicalData'
        },
        inspector: {
            containerId: 'inspectorPerformanceContainer',
            urlPath: 'inspector-stats',
            startDateId: 'inspectorStartDate',
            endDateId: 'inspectorEndDate',
            totalDisplayId: 'inspectorTotalDisplay',
            lastDataKey: 'lastInspectorData'
        }
    };

    const config = roleConfig[role];
    if (!config) {
        console.error(`Invalid role: ${role}`);
        return;
    }

    // Show loading indicator
    showBDLoadingIndicator(config.containerId);

    // Construct the base URL
    let url = `/stats/${config.urlPath}/${encodeURIComponent(identifier)}/${encodeURIComponent(period)}`;

    // Handle custom date range
    if (period === 'custom') {
        const startDate = document.getElementById(`${role}StartDate`).value;
        const endDate = document.getElementById(`${role}EndDate`).value;

        if (!startDate || !endDate) {
            showNotification('Please select both start and end dates', 'error');
            hideBDLoadingIndicator(config.containerId);
            return;
        }

        if (new Date(startDate) > new Date(endDate)) {
            showNotification('Start date cannot be after end date', 'error');
            hideBDLoadingIndicator(config.containerId);
            return;
        }
    }

    [from, to] = getStartAndEndDate(period, config.startDateId, config.endDateId);
    url += `?from=${from}&to=${to}`;
    // Fetch data from the server
    fetch(url, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            hideBDLoadingIndicator(config.containerId);
            updateRoleMetricsDisplay(role, data);

            const isNew = JSON.stringify(data) !== JSON.stringify(window[config.lastDataKey]);

            if (isNew) {
                window[config.lastDataKey] = data;

                if (hasMeaningfulInspectionData(data)) {
                    const totalDisplay = document.getElementById(config.totalDisplayId);
                    if (totalDisplay) {
                        totalDisplay.classList.add('bd-total-highlight');
                        setTimeout(() => totalDisplay.classList.remove('bd-total-highlight'), 1500);
                    }
                    showNotification(`${role.charAt(0).toUpperCase() + role.slice(1)} data updated successfully`, 'success');
                    updateComparisonChart();
                } else {
                    showNotification(`No data available for selected ${role}`, 'warning');
                }
            }
        })
        .catch(error => {
            hideBDLoadingIndicator(config.containerId);
            console.error('Error:', error);
            showNotification(`Failed to fetch ${role} data. Please try again.`, 'error');
        });
}
    // Update the fetch functions to match your API endpoints
    function fetchCoordinatorMetricData(coordinatorEmail, period) {
        fetchMetricData('coordinator', coordinatorEmail, period)
    }

    function fetchTechnicalMetricData(techCoordinatorId, period) {
        fetchMetricData('technical', techCoordinatorId, period)
    }

    function fetchInspectorMetricData(inspectorEmail, period) {
        fetchMetricData('inspector', inspectorEmail, period)
    }

    function hasMeaningfulInspectionData(data) {
        return data.totalInspections > 0 ||
               data.newInspections > 0 ||
               data.completedInspections > 0 ||
               data.ongoingInspections > 0 ||
               data.rejectedInspections > 0;
    }

    function updateRoleMetricsDisplay(roleType, data) {
        let containerId;
        if (roleType === 'technical') {
            containerId = 'technicalCoordinatorContainer';
        } else {
            containerId = `${roleType}PerformanceContainer`;
        }

        const container = document.getElementById(containerId);

        // Get the selected name from the dropdown
        let selectedName = "";
        if (roleType === 'coordinator') {
            const select = document.getElementById('coordinatorSelect');
            selectedName = select.options[select.selectedIndex].text;
        } else if (roleType === 'technical') {
            const select = document.getElementById('technicalCoordinatorSelect');
            selectedName = select.options[select.selectedIndex].text;
        } else if (roleType === 'inspector') {
            const select = document.getElementById('inspectorSelect');
            selectedName = select.options[select.selectedIndex].text;
        }

        // Format period text
        const periodText = formatPeriodText(data.period);

        // Create the metrics display HTML
        const metricsHTML = `
            <div class="bd-metrics-display">
                <div class="bd-metrics-header">
                    <h3>${selectedName}</h3>
                    <span class="bd-period-label">${periodText}</span>
                </div>
                <div class="bd-metrics-total">
                    <span class="bd-total-number">${data.totalInspections}</span>
                    <span class="bd-total-label">Total Inspections</span>
                </div>
                <div class="bd-metrics-breakdown">
                    <div class="bd-metric-item">
                        <span class="bd-metric-value">${data.newInspections || 0}</span>
                        <span class="bd-metric-label">New</span>
                    </div>
                    <div class="bd-metric-item">
                        <span class="bd-metric-value">${data.ongoingInspections || 0}</span>
                        <span class="bd-metric-label">Ongoing</span>
                    </div>
                    <div class="bd-metric-item">
                        <span class="bd-metric-value">${data.completedInspections || 0}</span>
                        <span class="bd-metric-label">Awarded</span>
                    </div>
                    <div class="bd-metric-item">
                        <span class="bd-metric-value">${data.rejectedInspections || 0}</span>
                        <span class="bd-metric-label">Rejected</span>
                    </div>
                </div>
            </div>
        `;

        container.innerHTML = metricsHTML;
    }

    // Helper function to format period text
    function formatPeriodText(period) {
        switch(period) {
            case 'WEEK': return 'Last 7 Days';
            case 'MONTH': return 'Last 30 Days';
            case 'QUARTER': return 'Last Quarter';
            case 'YEAR': return 'Year to Date';
            case 'CUSTOM': return 'Custom Range';
            default: return period;
        }
    }

function exportBDData(role, format) {
    const roleConfig = {
        coordinator: {
            selectId: 'coordinatorSelect',
            periodSelectId: 'coordinatorPeriodSelect',
            startDateId: 'startDate',
            endDateId: 'endDate',
            reportPath: '/stats/coordinator-report',
            container: 'coordinatorPerformanceContainer'
        },
        technical: {
            selectId: 'technicalCoordinatorSelect',
            periodSelectId: 'technicalPeriodSelect',
            startDateId: 'technicalStartDate',
            endDateId: 'technicalEndDate',
            reportPath: '/stats/technical-coordinator-report',
            container: 'technicalCoordinatorContainer'
        },
        inspector: {
            selectId: 'inspectorSelect',
            periodSelectId: 'inspectorPeriodSelect',
            startDateId: 'inspectorStartDate',
            endDateId: 'inspectorEndDate',
            reportPath: '/stats/inspector-report',
            container: 'inspectorPerformanceContainer'
        }
    };

    const config = roleConfig[role];
    if (!config) {
        console.error(`Invalid role: ${role}`);
        return;
    }

    const exportBtn = event.target.closest('.bd-export-btn');
    if (!exportBtn) return;

    const selectElement = document.getElementById(config.selectId);
    const selectedValue = selectElement.value;
    const selectedName = selectElement.selectedOptions[0]?.text || 'report';
    const period = document.getElementById(config.periodSelectId).value;

    if (!selectedValue) {
        showNotification(`Please select a ${role} before exporting.`, 'warning');
        return;
    }
    const containerId = config.container;
    const totalInspections= document.querySelector(`#${containerId} > div > div.bd-metrics-total > span.bd-total-number`)?.innerText || 0;
    const newInspections= document.querySelector(`#${containerId} > div > div.bd-metrics-breakdown > div:nth-child(1) > span.bd-metric-value`)?.innerText || 0;
    const ongoingInspections= document.querySelector(`#${containerId} > div > div.bd-metrics-breakdown > div:nth-child(2) > span.bd-metric-value`)?.innerText || 0;
    const completedInspections= document.querySelector(`#${containerId} > div > div.bd-metrics-breakdown > div:nth-child(3) > span.bd-metric-value`)?.innerText || 0;
    const rejectedInspections= document.querySelector(`#${containerId} > div > div.bd-metrics-breakdown > div:nth-child(4) > span.bd-metric-value`)?.innerText || 0;

    // Disable button and show loading state
    exportBtn.disabled = true;
    const originalText = exportBtn.innerHTML;
    exportBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;

    // Build endpoint URL
    let endpoint = `${config.reportPath}/${encodeURIComponent(selectedValue)}/${encodeURIComponent(period)}/${format}?`+
                     `totalInspections=${totalInspections}&newInspections=${newInspections}` +
                                    `&completedInspections=${completedInspections}&ongoingInspections=${ongoingInspections}` +
                                    `&rejectedInspections=${rejectedInspections}`;
    // Generate filename
    const filename = `${selectedName.replace(/\s+/g, '_')}_report.${format === 'pdf' ? 'pdf' : 'xlsx'}`;

    // Fetch data and handle file download
    fetch(endpoint)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }
            return response.blob();
        })
        .then(blob => {
            if (blob.size === 0) {
                showNotification('No data available for report generation.', 'warning');
                return;
            }

            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(link.href);

            showNotification(`${format.toUpperCase()} downloaded successfully.`, 'success');
        })
        .catch(error => {
            console.error('Download failed:', error);
            showNotification(`Failed to export ${format.toUpperCase()} report.`, 'error');
        })
        .finally(() => {
            // Restore button state
            exportBtn.disabled = false;
            exportBtn.innerHTML = originalText;
        });
}

// Utility functions
function showBDLoadingIndicator(containerId) {
    const container = document.getElementById(containerId);

    // Create loading overlay if it doesn't exist
    if (!container.querySelector('.bd-loading-overlay')) {
        const loadingOverlay = document.createElement('div');
        loadingOverlay.className = 'bd-loading-overlay';
        loadingOverlay.innerHTML = `
            <div class="bd-loading-spinner">
                <i class="fas fa-circle-notch fa-spin"></i>
            </div>
            <div class="bd-loading-text">Loading data...</div>
        `;

        container.style.position = 'relative';
        container.appendChild(loadingOverlay);
    } else {
        container.querySelector('.bd-loading-overlay').style.display = 'flex';
    }
}

function hideBDLoadingIndicator(containerId) {
    const container = document.getElementById(containerId);
    const loadingOverlay = container.querySelector('.bd-loading-overlay');

    if (loadingOverlay) {
        loadingOverlay.style.display = 'none';
    }
}

function removeEmptyState(containerId) {
    const container = document.getElementById(containerId);
    const emptyState = container.querySelector('.bd-empty-state');

    if (emptyState) {
        container.removeChild(emptyState);
    }
}

function showNotification(message, type = 'info') {
    // Check if notification container exists, if not create it
    let notificationContainer = document.getElementById('bd-notification-container');
    if (!notificationContainer) {
        notificationContainer = document.createElement('div');
        notificationContainer.id = 'bd-notification-container';
        document.body.appendChild(notificationContainer);
    }

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `bd-notification bd-notification-${type}`;

    // Set icon based on type
    let icon;
    switch (type) {
        case 'success':
            icon = 'fa-check-circle';
            break;
        case 'error':
            icon = 'fa-times-circle';
            break;
        case 'warning':
            icon = 'fa-exclamation-triangle';
            break;
        default:
            icon = 'fa-info-circle';
    }

    notification.innerHTML = `
        <div class="bd-notification-icon">
            <i class="fas ${icon}"></i>
        </div>
        <div class="bd-notification-content">
            ${message}
        </div>
        <div class="bd-notification-close">
            <i class="fas fa-times"></i>
        </div>
    `;

    // Add to container
    notificationContainer.appendChild(notification);

    // Add event listener to close button
    notification.querySelector('.bd-notification-close').addEventListener('click', function() {
        notification.classList.add('bd-notification-hiding');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    });

    // Auto-remove after delay
    setTimeout(() => {
        if (notification.parentNode) {
            notification.classList.add('bd-notification-hiding');
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }
    }, 5000);
}
function showComparisonLoader() {
    const loader = document.getElementById('comparisonChartLoader');
    if (loader) loader.style.display = 'flex';
}

function hideComparisonLoader() {
    const loader = document.getElementById('comparisonChartLoader');
    if (loader) loader.style.display = 'none';
}
function updateComparisonChart() {
    const chart = Chart.getChart('comparisonChart');
    if (!chart) return;

    const coordinator = document.getElementById('coordinatorSelect')?.value;
    const technical = document.getElementById('technicalCoordinatorSelect')?.value;
    const inspector = document.getElementById('inspectorSelect')?.value;

    // Only pass selected values if actually chosen (non-placeholder)
    const selectedCoordinator = coordinator !== "" ? coordinator : null;
    const selectedTechnical = technical !== "" ? technical : null;
    const selectedInspector = inspector !== "" ? inspector : null;

    const url = `/business/trend-data?coordinator=${encodeURIComponent(selectedCoordinator || "")}&technical=${encodeURIComponent(selectedTechnical || "")}&inspector=${encodeURIComponent(selectedInspector || "")}`;

    showComparisonLoader();

    fetch(url)
        .then(response => response.json())
        .then(data => {
            hideComparisonLoader();

            if (!data || !data.datasets || data.datasets.length === 0) {
                chart.data.labels = [];
                chart.data.datasets = [{
                    label: 'No Data Available',
                    data: [],
                    borderColor: '#e74a3b',
                    backgroundColor: 'rgba(231, 74, 59, 0.1)',
                    borderWidth: 2,
                    tension: 0.4,
                    fill: true
                }];
                chart.update();
                return;
            }

            chart.data.labels = data.labels;
            chart.data.datasets = data.datasets.map((ds, index) => ({
                label: ds.label,
                data: ds.data,
                borderColor: getLineColor(index),
                backgroundColor: 'rgba(0, 123, 255, 0.05)',
                borderWidth: 2,
                tension: 0.4,
                fill: true,
                pointRadius: 4,
                pointHoverRadius: 6
            }));

const titleParts = ['All Inspections'];
if (selectedCoordinator) {
    const select = document.getElementById('coordinatorSelect');
    const name = select.options[select.selectedIndex]?.text;
    if (name) titleParts.push(`${name} (Coordinator)`);
}
if (selectedTechnical) {
    const select = document.getElementById('technicalCoordinatorSelect');
    const name = select.options[select.selectedIndex]?.text;
    if (name) titleParts.push(`${name} (Technical)`);
}
if (selectedInspector) {
    const select = document.getElementById('inspectorSelect');
    const name = select.options[select.selectedIndex]?.text;
    if (name) titleParts.push(`${name} (Inspector)`);
}

chart.options.plugins.title = {
    display: true,
    text: titleParts.length > 1
        ? titleParts.join(' vs ')
        : 'All Inspections Trend',
    font: {
        size: 16,
        weight: 'bold'
    },
    padding: {
        top: 10,
        bottom: 20
    }
};
            chart.update();
        })
        .catch(err => {
            hideComparisonLoader();
            console.error("Trend data fetch failed", err);
            showNotification('Unable to load performance trends.', 'error');
        });
}

function getLineColor(index) {
    const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b'];
    return colors[index % colors.length];
}

// Helper function to generate random data for demonstration
function generateRandomData(count, min, max) {
    const data = [];
    for (let i = 0; i < count; i++) {
        data.push(Math.floor(Math.random() * (max - min + 1)) + min);
    }
    return data;
}

/*]]>*/


/** ================= Coordinator, Technical Coordinator, Inspector, Admin Dashboard =================== **/

    // Initialize date inputs with default values
    function initializeDateInputs(context, stats, reports) {

        if(stats) {
             // Set up event listeners for stats
            document.getElementById('periodSelect').addEventListener('change', handlePeriodChange);
            document.getElementById('applyCustomRangeBtn').addEventListener('click', applyCustomRange);
            document.getElementById('exportPdfBtn').addEventListener('click', () => exportData(context, 'pdf'));
            document.getElementById('exportExcelBtn').addEventListener('click', () => exportData(context, 'excel'));

            const today = new Date();
            const thirtyDaysAgo = new Date();
            thirtyDaysAgo.setDate(today.getDate() - 30);

            const formatDate = (date) => {
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            };

            document.getElementById('startDate').value = formatDate(thirtyDaysAgo);
            document.getElementById('endDate').value = formatDate(today);
        }

        if(reports) {
            // Set up event listeners for inspection reports
            document.getElementById('reportPeriodSelect').addEventListener('change', handleReportPeriodChange);
            document.getElementById('exportReportExcelBtn').addEventListener('click', () => exportData('reports', 'excel'));

             // Set up event listeners for iso reports
            document.getElementById('isoPeriodSelect').addEventListener('change', handleISOPeriodChange);
            document.getElementById('exportISOReportExcelBtn').addEventListener('click', () => exportData('iso', 'excel'));
        }
    }

    // Handle period change
    function handlePeriodChange() {
        const periodSelect = document.getElementById('periodSelect');
        const customRangeContainer = document.getElementById('customRangeContainer');

        if (periodSelect.value === 'custom') {
            customRangeContainer.style.display = 'block';
        } else {
            customRangeContainer.style.display = 'none';
            fetchDataForPeriod(periodSelect.value);
        }
    }

    // Handle report period change
    function handleReportPeriodChange() {
        const periodSelect = document.getElementById('reportPeriodSelect');
        const reportsCustomRangeContainer = document.getElementById('reportsCustomRangeContainer');

        if (periodSelect.value === 'custom') {
            document.getElementById('exportReportExcelBtn').disabled = true;
            reportsCustomRangeContainer.style.display = 'block';
        } else {
            document.getElementById('exportReportExcelBtn').disabled = false;
            reportsCustomRangeContainer.style.display = 'none';
        }
    }

   // Handle iso period change
    function handleISOPeriodChange() {
            const periodSelect = document.getElementById('isoPeriodSelect');
            const reportsCustomRangeContainer = document.getElementById('isoReportsCustomRangeContainer');

            if (periodSelect.value === 'custom') {
                document.getElementById('exportISOReportExcelBtn').disabled = true;
                reportsCustomRangeContainer.style.display = 'block';
            } else {
                document.getElementById('exportISOReportExcelBtn').disabled = false;
                reportsCustomRangeContainer.style.display = 'none';
            }
        }

    // Apply custom date range
    function applyCustomRange() {
        const period = document.getElementById('periodSelect').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        if (startDate && endDate) {
        if (new Date(startDate) > new Date(endDate)) {
        showNotification('Start date cannot be after end date', 'error');
            return;
          }
            fetchDataForCustomRange(period, startDate, endDate);
        } else {
            showNotification('Please select both start and end dates', 'error');
        }
    }

     // Apply custom date range
    function applyReportsCustomRange() {
        const period = document.getElementById('reportPeriodSelect').value;
        const startDate = document.getElementById('reportStartDate').value;
        const endDate = document.getElementById('reportEndDate').value;

        if (startDate && endDate) {
            if (new Date(startDate) > new Date(endDate)) {
                showNotification('Start date cannot be after end date', 'error');
                return;
              }
            document.getElementById('exportReportExcelBtn').disabled = false;
        } else {
            showNotification('Please select both start and end dates', 'error');
        }
    }

     // Apply custom date range
    function applyISOReportsCustomRange() {
        const period = document.getElementById('isoPeriodSelect').value;
        const startDate = document.getElementById('isoReportStartDate').value;
        const endDate = document.getElementById('isoReportEndDate').value;

        if (startDate && endDate) {
            if (new Date(startDate) > new Date(endDate)) {
                showNotification('Start date cannot be after end date', 'error');
                return;
              }
            document.getElementById('exportISOReportExcelBtn').disabled = false;
        } else {
            showNotification('Please select both start and end dates', 'error');
        }
    }


    // Fetch data for a specific period
    function fetchDataForPeriod(period) {
        showLoadingIndicator();
        if (!period) {
            showNotification('Invalid Period Selection.', 'warning');
            resetExportButton(exportBtn, originalText);
            return;
        }
        [from, to] = getStartAndEndDate(period, 'startDate', 'endDate');
        const context = {
                   "Coordinator Dashboard": "coordinator",
                   "Technical Coordinator Dashboard": "technical-coordinator",
                   "Inspector Dashboard": "inspector"
               }[document.title] || null;

        if(context == null) {
          showNotification('Failed to load performance data', 'error');
        }

        var userEmail = document.querySelector('.user-email').textContent;

        if(context == 'technical-coordinator') {
            userEmail = document.getElementById('employeeId').value;
            if (!userEmail) {
                showNotification('Employee ID not found', 'error');
                return;
            }
        }

        const url = `/stats/${context}-stats/${encodeURIComponent(userEmail)}/${period}?from=${from}&to=${to}`

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }
                return response.json();
            })
            .then(data => {
                updateMetricsDisplay(data);
                hideLoadingIndicator();
                showNotification('Data updated successfully', 'success');
            })
            .catch(error => {
                console.error('Error fetching data:', error);
                hideLoadingIndicator();
                showNotification('Failed to load performance data', 'error');
            });
    }

    // Update the metrics display with the latest data
    function updateMetricsDisplay(data) {
        // Update the total inspections count
        const totalElement = document.getElementById('totalInspections');
        if (totalElement) {
            totalElement.textContent = data['totalInspections'] || 0;
            totalElement.classList.add('cd-total-highlight');
            setTimeout(() => totalElement.classList.remove('cd-total-highlight'), 1500);
        }

        // Update individual metrics
       const metrics = {
            'newInspections': 'newInspections',
            'ongoingInspections': 'ongoingInspections',
            'awardedInspections': 'completedInspections',
            'rejectedInspections': 'rejectedInspections'
        };

        for (const [elementId, dataKey] of Object.entries(metrics)) {
            const element = document.getElementById(elementId);
            if (element) {
                element.textContent = data[dataKey] || 0;
            }
        }
    }

    // Fetch data for custom date range
    function fetchDataForCustomRange(period, from, to) {
    showLoadingIndicator();

    const context = {
               "Coordinator Dashboard": "coordinator",
               "Technical Coordinator Dashboard": "technical-coordinator",
               "Inspector Dashboard": "inspector"
           }[document.title] || null;

    if(context == null) {
      showNotification('Failed to load performance data', 'error');
    }

    var email = document.querySelector('.user-email').textContent;
    from += 'T00:00:00.0001';
    to += 'T23:59:59.9999';

    if(context == 'technical-coordinator') {
        email = document.getElementById('employeeId').value;
    }
    let url = `/stats/${context}-stats/${encodeURIComponent(email)}/${period}?from=${from}&to=${to}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            updateMetricsDisplay(data);
            hideLoadingIndicator();
            showNotification('Custom range data loaded', 'success');
        })
        .catch(error => {
            console.error('Error fetching custom range data:', error);
            hideLoadingIndicator();
            showNotification('Failed to load custom range data', 'error');
        });
}

    // Show loading indicator
    function showLoadingIndicator() {
          const container = document.querySelector('.cd-performance-metrics-container');
    const overlay = container.querySelector('.cd-loading-overlay');
    const content = container.querySelector('.cd-metrics-display');

    if (overlay) {
        content.style.opacity = '0.5';
        overlay.style.display = 'flex';
    }
    }

    // Hide loading indicator
    function hideLoadingIndicator() {
        const container = document.querySelector('.cd-performance-metrics-container');
    const overlay = container.querySelector('.cd-loading-overlay');
    const content = container.querySelector('.cd-metrics-display');

    if (overlay) {
        content.style.opacity = '1';
        overlay.style.display = 'none';
    }
    }

    // Show notification
    function showNotification(message, type = 'info') {
        const notificationContainer = document.getElementById('cd-notification-container') || createNotificationContainer();

        const notification = document.createElement('div');
        notification.className = `cd-notification cd-notification-${type}`;

        let icon;
        switch (type) {
            case 'success': icon = 'fa-check-circle'; break;
            case 'error': icon = 'fa-times-circle'; break;
            case 'warning': icon = 'fa-exclamation-triangle'; break;
            default: icon = 'fa-info-circle';
        }

        notification.innerHTML = `
            <div class="cd-notification-icon">
                <i class="fas ${icon}"></i>
            </div>
            <div class="cd-notification-content">
                ${message}
            </div>
            <div class="cd-notification-close">
                <i class="fas fa-times"></i>
            </div>
        `;

        notificationContainer.appendChild(notification);

        // Add close event
        notification.querySelector('.cd-notification-close').addEventListener('click', function() {
            notification.classList.add('cd-notification-hiding');
            setTimeout(() => notification.remove(), 300);
        });

        // Auto-remove after 5 seconds
        setTimeout(() => {
            notification.classList.add('cd-notification-hiding');
            setTimeout(() => notification.remove(), 300);
        }, 5000);
    }

    // Create notification container if it doesn't exist
    function createNotificationContainer() {
        const container = document.createElement('div');
        container.id = 'cd-notification-container';
        document.body.appendChild(container);
        return container;
    }


    function exportData(context, format) {
    const exportBtn = event.target.closest('.bd-export-btn') || event.target;
    if (!exportBtn) return;
    exportBtn.disabled = true;
    const originalText = exportBtn.innerHTML;
    exportBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;

    const dashboardHeader = document.querySelector('.section-header span');
    const contextName = dashboardHeader
        ? dashboardHeader.textContent.replace('My Performance Dashboard - ', '')
        : `${context}_report`;

    const encodedName = encodeURIComponent(contextName.replace(/\s+/g, '_'));

    const getInspectionMetrics = () => ({
        totalInspections: document.getElementById("totalInspections")?.innerText || 0,
        newInspections: document.getElementById("newInspections")?.innerText || 0,
        completedInspections: document.getElementById("awardedInspections")?.innerText || 0,
        ongoingInspections: document.getElementById("ongoingInspections")?.innerText || 0,
        rejectedInspections: document.getElementById("rejectedInspections")?.innerText || 0,
    });

    const buildFilename = (suffix) => `${encodedName}_report.${suffix}`;
    const showPeriodWarning = () => {
        showNotification('Please select a period range before exporting.', 'warning');
        resetExportButton(exportBtn, originalText);
    };
    
     const showPhaseWarning = () => {
            showNotification('Selected report will be available in next release', 'warning');
            resetExportButton(exportBtn, originalText);
        };

    let endpoint, filename, client, period, startDate, endDate, isoType;

    switch (context) {
        case 'coordinator':
        case 'inspector': {
            const metrics = getInspectionMetrics();
            const email = document.querySelector('.user-email')?.textContent;
            period = document.getElementById('periodSelect')?.value || '';
            if (!period) return showPeriodWarning();
            endpoint = `/stats/${context}-report/${email}/${period}/${format}?` +
                `totalInspections=${metrics.totalInspections}&newInspections=${metrics.newInspections}` +
                `&completedInspections=${metrics.completedInspections}&ongoingInspections=${metrics.ongoingInspections}` +
                `&rejectedInspections=${metrics.rejectedInspections}`;
            filename = buildFilename(format === 'pdf' ? 'pdf' : 'xlsx');
            break;
        }
        case 'technical-coordinator': {
            const metrics = getInspectionMetrics();
            const employeeID = document.getElementById('employeeId')?.value;
            if (!employeeID) {
                showNotification('Employee ID not found', 'error');
                resetExportButton(exportBtn, originalText);
                return;
            }
            period = document.getElementById('periodSelect')?.value || '';
            if (!period) return showPeriodWarning();
            endpoint = `/stats/technical-coordinator-report/${employeeID}/${period}/${format}?` +
                `totalInspections=${metrics.totalInspections}&newInspections=${metrics.newInspections}` +
                `&completedInspections=${metrics.completedInspections}&ongoingInspections=${metrics.ongoingInspections}` +
                `&rejectedInspections=${metrics.rejectedInspections}`;
            filename = buildFilename(format === 'pdf' ? 'pdf' : 'xlsx');
            break;
        }
        case 'reports': {
            period = document.getElementById('reportPeriodSelect')?.value;
            if (!period) return showPeriodWarning();
            [startDate, endDate] = getStartAndEndDate(period, 'reportStartDate', 'reportEndDate');
            endpoint = `/reports/inspections/${period}/${startDate}/${endDate}/${format}`;
            client = document.getElementById('clientSelect')?.value;
            if (client && client !== 'all') {
                endpoint += `?client=${client}`;
            }
            filename = `inspections_report_${period}_${startDate}_${endDate}.${format === 'pdf' ? 'pdf' : 'xlsx'}`;
            break;
        }
        case 'iso': {
            period = document.getElementById('isoPeriodSelect')?.value;
            isoType = document.getElementById('isoSelect')?.value;
            const validIsoTypes = ['orderRegister', 'enquiryQuotationOrder', 'inspectionCallStatus'];

            if (!isoType || !validIsoTypes.includes(isoType)) {
                return showPhaseWarning();
            }
            if (!period) return showPeriodWarning();

            [startDate, endDate] = getStartAndEndDate(period, 'isoReportStartDate', 'isoReportEndDate');
            endpoint = `/reports/iso/${isoType}/${period}/${startDate}/${endDate}/${format}`;
            filename = `${isoType}_report_${period}_${startDate}_${endDate}.${format === 'pdf' ? 'pdf' : 'xlsx'}`;
            break;
        }
        default:
            showNotification('Invalid context for export operation.', 'error');
            resetExportButton(exportBtn, originalText);
            return;
    }

    fetch(endpoint)
        .then(response => {
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            return response.blob();
        })
        .then(blob => {
            if (blob.size === 0) {
                showNotification('No data available for report generation', 'warning');
                return;
            }
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(link.href);
            showNotification(`${format.toUpperCase()} downloaded successfully.`, 'success');
        })
        .catch(error => {
            console.error('Export failed:', error);
            showNotification(`Failed to export ${format.toUpperCase()} report.`, 'error');
        })
        .finally(() => {
            resetExportButton(exportBtn, originalText);
        });
    }

    function resetExportButton(button, originalText) {
        button.disabled = false;
        button.innerHTML = originalText;
    }

    function getStartAndEndDate(period, customStartDateId, customEndDateId) {
        let from, to;
        const today = new Date();

        switch (period.toUpperCase()) {
            case 'TODAY':
                from = to = today.toISOString().split('T')[0];
                break;
            case 'WEEK':
                to = today.toISOString().split('T')[0];
                from = new Date(today.setDate(today.getDate() - 7)).toISOString().split('T')[0];
                break;
            case 'MONTH':
                to = today.toISOString().split('T')[0];
                from = new Date(today.setDate(today.getDate() - 30)).toISOString().split('T')[0];
                break;
            case 'YEAR':
                to = today.toISOString().split('T')[0];
                from = new Date(today.setDate(today.getDate() - 365)).toISOString().split('T')[0];
                break;
            case 'CUSTOM':
                from = new Date(document.getElementById(customStartDateId).value).toISOString().split('T')[0];
                to = new Date(document.getElementById(customEndDateId).value).toISOString().split('T')[0];
                break;
            default:
                throw new Error(`Invalid period: ${period}`);
        }

        from += 'T00:00:00.0001';
        to += 'T23:59:59.9999';
        return [from, to];
    }

    function togglePerformanceContainer(element) {
        const performanceContainer = document.getElementById('performanceContainer');
        if (performanceContainer.style.display === 'none') {
            performanceContainer.style.display = 'block';
            if (document.getElementById('reportsContainer')) {
                document.getElementById('reportsContainer').style.display = 'none';
            }
            document.querySelectorAll('.dashboard-tabs .tab').forEach(tab => tab.classList.remove('active'));
            element.classList.add('active');
        } else {
            performanceContainer.style.display = 'none';
            element.classList.remove('active');
        }
    }

    function toggleReportsContainer(element) {
        const reportsContainer = document.getElementById('reportsContainer');
        if (reportsContainer.style.display === 'none') {
            reportsContainer.style.display = 'block';

            if (document.getElementById('performanceContainer')) {
                document.getElementById('performanceContainer').style.display = 'none';
            }
            document.querySelectorAll('.dashboard-tabs .tab').forEach(tab => tab.classList.remove('active'));
            element.classList.add('active');
        } else {
            reportsContainer.style.display = 'none';
            element.classList.remove('active');
        }
    }

/** ============================ Inspectors Management ============================== **/

function exportInspectorsData(context, format) {
    const exportBtn = event.target.closest('.bd-export-btn') || event.target;
    if (!exportBtn) return;
    exportBtn.disabled = true;
    const originalText = exportBtn.innerHTML;
    exportBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;
    let endpoint, filename;

    endpoint = `/reports/inspectors`;
    filename = 'inspectors_report.xlsx'

    fetch(endpoint)
        .then(response => {
            if (!response.ok) throw new Error(`Error: ${response.status}`);
            return response.blob();
        })
        .then(blob => {
            if (blob.size === 0) {
                showNotification('No data available for report generation', 'warning');
                return;
            }
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            window.URL.revokeObjectURL(link.href);
            showNotification(`${format.toUpperCase()} downloaded successfully.`, 'success');
        })
        .catch(error => {
            console.error('Export failed:', error);
            showNotification(`Failed to export ${format.toUpperCase()} report.`, 'error');
        })
        .finally(() => {
            resetExportButton(exportBtn, originalText);
        });
    }

// Define the function that was being called via onclick
function openAgreementDialog() {
    $('#agreementModal').modal('show');
}

document.addEventListener('DOMContentLoaded', function() {
    // Initialize modal using jQuery (since you're using Bootstrap 4)
    const agreementModal = $('#agreementModal');

    // Get the correct buttons (using the IDs/classes from your HTML)
    const generateAgreementBtn = document.querySelector('.btn.btn-primary.ml-2');
    const downloadAgreementBtn = document.querySelector('#agreementModal .btn-primary');
    const inspectorSelect = document.getElementById('inspectorSelect');

    // Open modal when Generate Agreement button is clicked
    if (generateAgreementBtn) {
        generateAgreementBtn.addEventListener('click', function() {
            agreementModal.modal('show');
        });
    }

    // Handle download when Download button is clicked
    if (downloadAgreementBtn) {
        downloadAgreementBtn.addEventListener('click', async function() {
            await downloadAgreement();
        });
    }

    async function downloadAgreement() {
        const selectedOption = inspectorSelect.options[inspectorSelect.selectedIndex];

        if (!selectedOption.value) {
            showNotification(`Please select an inspector first.`, 'warning');
            return;
        }

        // Get all required data from data attributes
        const params = {
            inspectorId: selectedOption.value,
            inspectorName: selectedOption.dataset.name,
            address: selectedOption.dataset.address,
            email: selectedOption.dataset.email,
            phone: selectedOption.dataset.phone,
            country: selectedOption.dataset.country
        };

        try {
            // Show loading state
            const downloadBtn = $('#agreementModal .btn-primary');
            downloadBtn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Generating...');

            // First download: Inspector Agreement
            const response = await fetch('/reports/inspectors/agreement?' + new URLSearchParams(params), {
                method: 'GET',
                headers: {
                    'Accept': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            // Create download for Inspector Agreement
            const blob = await response.blob();
            await downloadFile(blob, `Inspector_Agreement_${params.inspectorName.replace(/\s+/g, '_')}.docx`);

            // Only proceed to download Impartiality Agreement if the first was successful
            await downloadImpartialityAgreement(params.inspectorName);

            showNotification(`Agreements downloaded successfully.`, 'success');
        } catch (error) {
            console.error('Download failed:', error);
            showNotification(`Failed to generate agreement. Please try again.`, 'error');
        } finally {
            // Reset button state
            const downloadBtn = $('#agreementModal .btn-primary');
            downloadBtn.prop('disabled', false).text('Download');
            agreementModal.modal('hide');
        }
    }

    // Separate function to download Impartiality Agreement
    async function downloadImpartialityAgreement(inspectorName) {
        try {
            const response = await fetch('/reports/inspectors/impartiality-doc?inspectorName=' + encodeURIComponent(inspectorName), {
                method: 'GET',
                headers: {
                    'Accept': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const blob = await response.blob();
            await downloadFile(blob, `Impartiality_Agreement_${inspectorName.replace(/\s+/g, '_')}.docx`);
        } catch (error) {
            console.error('Impartiality agreement download failed:', error);
            throw error; // Re-throw to handle in the calling function
        }
    }

    // Helper function to handle file download
    function downloadFile(blob, filename) {
        return new Promise((resolve) => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();

            // Clean up
            setTimeout(() => {
                document.body.removeChild(a);
                window.URL.revokeObjectURL(url);
                resolve();
            }, 100);
        });
    }
});
