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
    
    
    // Check if the window title is "Coordinator Dashboard"
    if (document.title === "Coordinator Dashboard" || document.title === "Admin Dashboard" || document.title === "Business Dashboard") {
       // Initialize date inputs
        initializeDateInputs();
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



/** ================= Coordinator Dashboard =================== **/

    // Initialize date inputs with default values
    function initializeDateInputs() {
         // Set up event listeners for stats
        document.getElementById('periodSelect').addEventListener('change', handlePeriodChange);
        document.getElementById('applyCustomRangeBtn').addEventListener('click', applyCustomRange);
        document.getElementById('exportPdfBtn').addEventListener('click', () => exportCoordinatorData('pdf'));
        document.getElementById('exportExcelBtn').addEventListener('click', () => exportCoordinatorData('excel'));

        // Set up event listeners for reports
        document.getElementById('reportPeriodSelect').addEventListener('change', handleReportPeriodChange);
        document.getElementById('exportReportExcelBtn').addEventListener('click', () => exportReports('excel'));

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
            fetchDataForCustomRange(period,startDate, endDate);
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



    // Fetch data for a specific period
    function fetchDataForPeriod(period) {
        showLoadingIndicator();

        const coordinatorEmail = document.querySelector('.user-email').textContent;

        fetch(`/stats/coordinator-stats/${encodeURIComponent(coordinatorEmail)}/${period}`)
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
function fetchDataForCustomRange(period, startDate, endDate) {
    showLoadingIndicator();

    const coordinatorEmail = document.querySelector('.user-email').textContent;
    let url = `/stats/coordinator-stats/${encodeURIComponent(coordinatorEmail)}/${period}?startDate=${startDate}&endDate=${endDate}`;

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

    function exportCoordinatorData(format) {
        const exportBtn = event.target.closest('.bd-export-btn');
        if (!exportBtn) return;
        exportBtn.disabled = true;
        const originalText = exportBtn.innerHTML;
        exportBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;
        const dashboardHeader = document.querySelector('.section-header span');
        const coordinatorName = dashboardHeader ?
            dashboardHeader.textContent.replace('My Performance Dashboard - ', '') :
            'coordinator_report';

        let period = document.getElementById('periodSelect').value;
         // If no period is selected, use current month as default (yyyy-MM)
       if (!period) {
        showNotification('Please select a period range before exporting.', 'warning');
            exportBtn.disabled = false;
            exportBtn.innerHTML = originalText;
            return;
       }
        const employeeName = /*[[${employeeName}]]*/ coordinatorName;
        const encodedName = encodeURIComponent(employeeName.replace(/\s+/g, '_'));
        const coordinatorEmail = document.querySelector('.user-email').textContent;

        const endpoint = `/stats/coordinator-report/${coordinatorEmail}/${period}/${format}`;
        const filename = `${encodedName}_report.${format === 'pdf' ? 'pdf' : 'xlsx'}`;

        fetch(endpoint)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error: ${response.status}`);
                }
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
                console.error('Download failed:', error);
                showNotification(`Failed to export ${format.toUpperCase()} report.`, 'error');
            })
            .finally(() => {
                exportBtn.disabled = false;
                 exportBtn.innerHTML = originalText;
            });
    }
    function exportReports(format) {
        const exportBtn = event.target;
        exportBtn.disabled = true;
        const originalText = exportBtn.innerHTML;
        exportBtn.innerHTML = `<i class="fas fa-spinner fa-spin"></i> Processing...`;
        let period = document.getElementById('reportPeriodSelect').value;
         // If no period is selected, use current month as default (yyyy-MM)
       if (!period) {
        showNotification('Please select a period range before exporting.', 'warning');
         exportBtn.disabled = false;
         exportBtn.innerHTML = originalText;
         return;
       }
       
        let from, to;

        switch (period.toUpperCase()) {
            case 'TODAY':
                to = new Date().toISOString().split('T')[0];
                from = to;
                break;
            case 'WEEK':
                to = new Date().toISOString().split('T')[0];
                from = new Date(new Date().setDate(new Date().getDate() - 7)).toISOString().split('T')[0];
                break;
            case 'MONTH':
                to = new Date().toISOString().split('T')[0];
                from = new Date(new Date().setDate(new Date().getDate() - 30)).toISOString().split('T')[0];
                break;
            case 'YEAR':
                to = new Date().toISOString().split('T')[0];
                from = new Date(new Date().setDate(new Date().getDate() - 365)).toISOString().split('T')[0];
            case 'CUSTOM':
                from = new Date(document.getElementById('reportStartDate').value).toISOString().split('T')[0];
                to = new Date(document.getElementById('reportEndDate').value).toISOString().split('T')[0];
                break;
            default:
                throw new Error(`Invalid period: ${period}`);
        }
        from += 'T00:00:00.0001';
        to += 'T23:59:59.9999';
        const endpoint = `/reports/inspections/${period}/${from}/${to}/${format}`;
        const filename = `inspections_report_${period}_${from}_${to}.${format === 'pdf' ? 'pdf' : 'xlsx'}`;

        fetch(endpoint)
            .then(response => {
                if (!response.ok) {
                    throw new Error(`Error: ${response.status}`);
                }
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
                console.error('Download failed:', error);
                showNotification(`Failed to export ${format.toUpperCase()} report.`, 'error');
            })
            .finally(() => {
                exportBtn.disabled = false;
                exportBtn.innerHTML = originalText;
            });
    }

    function togglePerformanceContainer(element) {
        const performanceContainer = document.getElementById('performanceContainer');
        if (performanceContainer.style.display === 'none') {
            performanceContainer.style.display = 'block';
            document.getElementById('reportsContainer').style.display = 'none';
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
            document.getElementById('performanceContainer').style.display = 'none';
            document.querySelectorAll('.dashboard-tabs .tab').forEach(tab => tab.classList.remove('active'));
            element.classList.add('active');
        } else {
            reportsContainer.style.display = 'none';
            element.classList.remove('active');
        }
    }


